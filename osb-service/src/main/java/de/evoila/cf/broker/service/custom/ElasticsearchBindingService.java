package de.evoila.cf.broker.service.custom;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.credential.UsernamePasswordCredential;
import de.evoila.cf.broker.repository.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.custom.constants.CredentialConstants;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import de.evoila.cf.security.credentials.CredentialStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.evoila.cf.broker.service.custom.ElasticsearchBindingService.ClientMode.CLIENT_MODE_IDENTIFIER;
import static de.evoila.cf.broker.service.custom.ElasticsearchBindingService.ClientMode.EGRESS;

/**
 * @author Johannes Hiemer.
 */
@Service
public class ElasticsearchBindingService extends BindingServiceImpl {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String X_PACK_USERS_URI_PATTERN = "%s/_xpack/security/user";
    private static final String HEALTH_ENDPOINT_URI_PATTERN = "%s/_cluster/health";
    private static final String DEFAULT_ROLE = "superuser";
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchBindingService.class);
    private static final String URI = "uri";

    private final CredentialStore credentialStore;

    ElasticsearchBindingService(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository,
                                ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository,
                                JobRepository jobRepository, AsyncBindingService asyncBindingService,
                                PlatformRepository platformRepository, CredentialStore credentialStore) {
        super(bindingRepository, serviceDefinitionRepository, serviceInstanceRepository,
                routeBindingRepository, jobRepository, asyncBindingService, platformRepository);

        this.credentialStore = credentialStore;
    }

    private static ClientMode getClientModeOrDefault(final Map<String, Object> map) {
        Object clientMode = null;
        if (map != null) {
            clientMode = map.get(CLIENT_MODE_IDENTIFIER);
        }

        if (clientMode == null) {
            log.warn(MessageFormat.format("Encountered no clientMode. Used instead default clientMode ''{0}''.", EGRESS.identifier));

            return EGRESS;
        }

        try {
            return ClientMode.byIdentifier(clientMode.toString());
        } catch (IllegalArgumentException e) {
            log.warn(MessageFormat.format("Encountered unknown clientMode {0}. Used instead default clientMode ''{1}''.", clientMode, EGRESS.identifier));

            return EGRESS;
        }
    }

    private static String prettifyForLog(ServiceInstanceBindingRequest r) {
        final BindResource br = r.getBindResource();

        String bindResourceMessage;
        if (br == null) {
            bindResourceMessage = "null";
        } else {
            bindResourceMessage = MessageFormat.format("'{' appGuid: {0}, route: {1} '}'", br.getAppGuid(), br.getRoute());
        }

        String message = MessageFormat.format("'{' serviceDefinitionId: {0}, planId: {1}, appGuid: {2}, bindResource: {3} '}'", r.getServiceDefinitionId(), r.getPlanId(), r.getAppGuid(), bindResourceMessage);

        return message;
    }

    @Override
    protected Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                    ServiceInstance serviceInstance, Plan plan, ServerAddress host) throws ServiceBrokerException {

        log.info(MessageFormat.format("Creating credentials for bind request {0}.", prettifyForLog(serviceInstanceBindingRequest)));

        final List<ServerAddress> hosts = serviceInstance.getHosts();
        final ClientMode clientMode = getClientModeOrDefault(serviceInstanceBindingRequest.getParameters());
        final String serverAddressFilter = clientModeToServerAddressFilter(clientMode, plan);

        final Map<String, Object> credentials = new HashMap<>();


        List<ServerAddress> filteredHosts;
        if (host != null) {
            // If explicit server address should be used for connection string (host != null), use this for endpoint.
            String endpoint = host.getIp() + ":" + host.getPort();
            filteredHosts = new ArrayList<>();
            filteredHosts.add(host);

            credentials.put("host", endpoint);
            credentials.put(CLIENT_MODE_IDENTIFIER, clientMode.identifier);
        } else {
            filteredHosts = ServiceInstanceUtils.filteredServerAddress(hosts, serverAddressFilter);

            final List<String> hostsAsString = filteredHosts.stream()
                    .map(h -> h.getIp() + ":" + h.getPort())
                    .collect(Collectors.toList());
            credentials.put("hosts", hostsAsString);
            credentials.put(CLIENT_MODE_IDENTIFIER, clientMode.identifier);
        }

        final String protocolMode;
        String userCredentials = "";

        if (ElasticsearchUtilities.planContainsXPack(plan)) {

            // Built-In User case
            if (ClientMode.isBuiltInUser(clientMode)) {
                log.info("Built-In user binding requested.");
                UsernamePasswordCredential builtInUserCredential = getCredentialForClientMode(clientMode, serviceInstance);
                credentials.put("username", builtInUserCredential.getUsername());
                credentials.put("password", builtInUserCredential.getPassword());

                log.info(MessageFormat.format("Return binding for built-in user ''{0}''.", builtInUserCredential.getUsername()));
                return credentials;
            }

            final String username = bindingId;
            final String password = generatePassword();
            final RestTemplate restTemplate;

            if (ElasticsearchUtilities.isHttpsEnabled(plan)) {
                protocolMode = HTTPS;
                restTemplate = getRestTemplateWithSSL();
            } else {
                protocolMode = HTTP;
                restTemplate = new RestTemplate();
            }

            // Prepare REST Template
            final BasicAuthorizationInterceptor basicAuthorizationInterceptor = getInterceptorWithCredentials(CredentialConstants.SUPER_ADMIN, serviceInstance);
            restTemplate.getInterceptors().add(basicAuthorizationInterceptor);

            boolean success = false;
            for (ServerAddress nodeAddress : filteredHosts) {
                final String userCreationUri = generateUsersUri(nodeAddress.getIp() + ":" + nodeAddress.getPort(), protocolMode);
                final String endpoint = String.format("%s:%s", nodeAddress.getIp(), nodeAddress.getPort());

                try {
                    log.info(MessageFormat.format("Try binding on host {0}:{1,number,#} with URI \"{2}\" ", nodeAddress.getIp(), nodeAddress.getPort(), userCreationUri));
                    addUserToElasticsearch(username, userCreationUri, password, restTemplate);
                    credentials.put("username", username);
                    credentials.put("password", password);
                    userCredentials = String.format("%s:%s@", username, password);

                    credentialStore.createUser(serviceInstance, bindingId, username, password); // Add user to credential store

                    success = true;
                } catch (ServiceBrokerException e) {
                    log.info(MessageFormat.format("Binding failed on host {0}:{1,number,#}. {2}", nodeAddress.getIp(), nodeAddress.getPort(), e.getMessage()));
                }

                if (success) {
                    restTemplate.getInterceptors().remove(basicAuthorizationInterceptor);

                    final String dbURL = String.format("%s://%s%s", protocolMode, userCredentials, endpoint);
                    credentials.put(URI, dbURL);

                    break;
                }
            }
            if (!success) {
                log.error("Binding failed on all available hosts.");
                throw new ServiceBrokerException("Binding failed on all available hosts.");
            }
        } else {
            protocolMode = HTTP;

            String endpoint = "";
            final RestTemplate restTemplate = new RestTemplate();

            for(ServerAddress address : filteredHosts) {
                String hostAsString = address.getIp() + ":" + address.getPort();
                ResponseEntity<String> response = restTemplate.getForEntity(generateHealthEndpointUri(hostAsString, protocolMode), String.class);

                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    endpoint = hostAsString;
                    break;
                }
            }

            if (endpoint.equals("")) {
                log.error("Binding failed. No available hosts.");
                throw new ServiceBrokerException("Binding failed. No available hosts.");
            }

            final String dbURL = String.format("%s://%s%s", protocolMode, userCredentials, endpoint);
            credentials.put(URI, dbURL);
        }

        log.info(MessageFormat.format("Finished creating credentials for bind request {0}.", prettifyForLog(serviceInstanceBindingRequest)));
        return credentials;
    }

    /**
     * Returns a BasicAuthorizationInterceptor with username and password. The password
     * gets extracted automatically.
     *
     * @param username the username, must not be null
     * @param serviceInstance the service instance, must not be null
     * @return a BasicAuthorizationInterceptor with username and password
     */
    private BasicAuthorizationInterceptor getInterceptorWithCredentials(String username, ServiceInstance serviceInstance) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty!");
        }
        if (serviceInstance == null) {
            throw new IllegalArgumentException("ServiceInstance must not be null!");
        }
        final String adminPassword = credentialStore.getUser(serviceInstance, username).getPassword();

        return new BasicAuthorizationInterceptor(username, adminPassword);
    }

    /**
     * Returns a RestTemplate, which is usable for SSL/TLS encrypted requests.
     *
     * @return a RestTemplate with SSL/TLS support
     */
    private RestTemplate getRestTemplateWithSSL() {
        final SSLContext sslcontext;
        try {
            sslcontext = SSLContexts.custom().loadTrustMaterial(null,
                    new TrustSelfSignedStrategy()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException();
        }

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                new String[]{"TLSv1"}, null, new NoopHostnameVerifier());

        final HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpclient);

        return new RestTemplate(factory);
    }

    private void addUserToElasticsearch(String bindingId, String userCreationUri, String password, RestTemplate restTemplate) throws ServiceBrokerException {
        final ElasticsearchUser user = new ElasticsearchUser(password, DEFAULT_ROLE);

        try {
            String bindingUserUri = userCreationUri + "/" + bindingId;
            ResponseEntity<?> entity = restTemplate.postForEntity(bindingUserUri, user, ElasticsearchUser.class);

            final HttpStatus statusCode = entity.getStatusCode();
            if (!statusCode.is2xxSuccessful()) {
                throw new ServiceBrokerException(
                        new ServiceInstanceBindingException(bindingId, statusCode, "Cannot create user for binding."));
            }
        } catch (RestClientException e) {
            throw new ServiceBrokerException("Cannot create user for binding. " + e.getMessage());
        }
    }

    private String generatePassword() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private String generateUsersUri(String endpoint, String protocolMode) {
        final String adminUri = String.format("%s://%s", protocolMode, endpoint);
        return String.format(X_PACK_USERS_URI_PATTERN, adminUri);
    }

    private String generateHealthEndpointUri(String endpoint, String protocolMode) {
        final String clusterUri = String.format("%s://%s", protocolMode, endpoint);
        return String.format(HEALTH_ENDPOINT_URI_PATTERN, clusterUri);
    }

    private String clientModeToServerAddressFilter(ClientMode m, Plan p) {
        switch (m) {
            case INGRESS:
                return p.getMetadata().getIngressInstanceGroup();
            case EGRESS:
            default:
                return p.getMetadata().getEgressInstanceGroup();
        }
    }

    @Override
    protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException {
        final List<ServerAddress> hosts = serviceInstance.getHosts();
        final ClientMode clientMode = getClientModeOrDefault(binding.getCredentials());
        final String serverAddressFilter = clientModeToServerAddressFilter(clientMode, plan);
        final String bindingId = binding.getId();
        final String protocolMode;
        final RestTemplate restTemplate;

        log.info(MessageFormat.format("Deleting binding ''{0}''.", bindingId));

        // Built-In User case
        if (ClientMode.isBuiltInUser(clientMode)) {
            log.info(MessageFormat.format("Binding ''{0}'' deleted.", bindingId));
            return;
        }

        if (ElasticsearchUtilities.planContainsXPack(plan)) {
            if (ElasticsearchUtilities.isHttpsEnabled(plan)) {
                protocolMode = HTTPS;
                restTemplate = getRestTemplateWithSSL();
            } else {
                protocolMode = HTTP;
                restTemplate = new RestTemplate();
            }

            // Prepare REST Template
            final BasicAuthorizationInterceptor basicAuthorizationInterceptor = getInterceptorWithCredentials(CredentialConstants.SUPER_ADMIN, serviceInstance);
            restTemplate.getInterceptors().add(basicAuthorizationInterceptor);

            boolean success = false;
            for (ServerAddress a : hosts) {
                final ServerAddress host = a;
                final String endpoint;

                if (host != null) {
                    // If explicit server address should be used for connection string (host != null), use this for endpoint.
                    endpoint = host.getIp() + ":" + host.getPort();

                } else {
                    endpoint = ServiceInstanceUtils.connectionUrl(ServiceInstanceUtils.filteredServerAddress(hosts, serverAddressFilter));
                }

                final String userCreationUri = generateUsersUri(endpoint, protocolMode);

                try {
                    log.info(MessageFormat.format("Try binding on host {0}:{1,number,#} with URI \"{2}\" ", a.getIp(), a.getPort(), userCreationUri));
                    deleteUserFromElasticsearch(bindingId, userCreationUri, restTemplate);

                    credentialStore.deleteCredentials(serviceInstance, bindingId);  // Delete user from credential store

                    success = true;
                } catch (ServiceBrokerException e) {
                    log.info(MessageFormat.format("Failed deleting binding ''{0}'' on endpoint ''{1}''. {2}", bindingId, endpoint, e.getMessage()));
                }

                if (success) {
                    restTemplate.getInterceptors().remove(basicAuthorizationInterceptor);
                    log.info(MessageFormat.format("Finished deleting binding ''{0}''.", bindingId));
                    break;
                }
            }
            if (!success) {
                log.info(MessageFormat.format("Can not delete binding ''{0}''. Problem with host!", bindingId));
                throw new ServiceBrokerException(MessageFormat.format("Can not delete binding ''{0}''. Problem with host!", bindingId));
            }
        } else {
            log.info(MessageFormat.format("Binding ''{0}'' deleted.", bindingId));
        }
    }

    private void deleteUserFromElasticsearch(String bindingId, String usersUri, RestTemplate restTemplate) throws ServiceBrokerException {
        final String deleteURI = String.format("%s/%s",usersUri,bindingId);

        try {
            restTemplate.delete(deleteURI);
        } catch (RestClientException e) {
            log.error("Cannot delete user for binding. " + e.getMessage());
            throw new ServiceBrokerException("Cannot delete user for binding. " + e.getMessage());
        }
    }

    /**
     * Returns a UsernamePasswordCredential associated with an client mode.
     * @param clientMode the client mode
     * @param serviceInstance the service instance
     * @return a UsernamePasswordCredential associated with an client mode
     */
    private UsernamePasswordCredential getCredentialForClientMode(ClientMode clientMode, ServiceInstance serviceInstance) {
        switch (clientMode) {
            case SUPERUSER:
                return credentialStore.getUser(serviceInstance, CredentialConstants.SUPER_ADMIN);
            case KIBANA:
                return credentialStore.getUser(serviceInstance, CredentialConstants.KIBANA_USER);
            case LOGSTASH:
                return credentialStore.getUser(serviceInstance, CredentialConstants.LOGSTASH_USER);
            default:
                throw new IllegalArgumentException(MessageFormat.format("ClientMode identifier ''{0}'' is not associated with an built-in user.", clientMode));
        }
    }

    protected enum ClientMode {
        EGRESS("egress"), INGRESS("ingress"),
        SUPERUSER("superuser"), KIBANA("kibana"), LOGSTASH("logstash");

        public static final String CLIENT_MODE_IDENTIFIER = "clientMode";

        private final String identifier;

        ClientMode(String identifier) {
            this.identifier = identifier;
        }

        /**
         * This method returns whether the ClientMode is a Built-In User or not.
         * @param clientMode the client mode to check
         * @return true if is built-in user, false otherwise
         */
        public static boolean isBuiltInUser(ClientMode clientMode) {
            return clientMode == SUPERUSER || clientMode == KIBANA || clientMode == LOGSTASH;
        }

        public static ClientMode byIdentifier(String identifier) {
            switch (identifier) {
                case "egress":
                    return EGRESS;
                case "ingress":
                    return INGRESS;
                case "superuser":
                    return SUPERUSER;
                case "kibana":
                    return KIBANA;
                case "logstash":
                    return LOGSTASH;
                default:
                    throw new IllegalArgumentException(MessageFormat.format("Unknown ClientMode identifier {0}.", identifier));
            }
        }
    }
}
