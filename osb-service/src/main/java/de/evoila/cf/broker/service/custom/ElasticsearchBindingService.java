/**
 *
 */
package de.evoila.cf.broker.service.custom;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.MessageFormat;
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

    public static final String PROPERTIES_ELASTICSEARCH_X_PACK_ENABLED = "properties.elasticsearch.x-pack.enabled";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String X_PACK_USERS_URI_PATTERN = "%s/x-pack/users";
    public static final String SUPER_ADMIN = "elastic";
    public static final String SUPERUSER_ROLE = "superuser";
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchBindingService.class);
    private static final String URI = "uri";
    private final RestTemplate restTemplate;

    public ElasticsearchBindingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static ClientMode getClientModeOrDefault(ServiceInstanceBindingRequest bindingRequest) {
        final Map<String, Object> parameters = bindingRequest.getParameters();
        Object clientMode = null;
        if (parameters != null) {
            clientMode = parameters.get(CLIENT_MODE_IDENTIFIER);
        }

        if (clientMode == null) {
            log.warn(MessageFormat.format("Encountered no clientMode when trying to bind request {0}. Used instead default clientMode ''{1}''.", prettifyForLog(bindingRequest), EGRESS.identifier));

            return EGRESS;
        }

        try {
            return ClientMode.valueOf(clientMode.toString());
        } catch (IllegalArgumentException e) {
            log.warn(MessageFormat.format("Encountered unknown clientMode {0} when trying to bind request {1}. Used instead default clientMode ''{2}''.", clientMode, prettifyForLog(bindingRequest), EGRESS.identifier));

            return EGRESS;
        }
    }

    private static String prettifyForLog(ServiceInstanceBindingRequest r) {
        final BindResource br = r.getBindResource();

        String bindResourceMessage;
        if(br == null) {
            bindResourceMessage = "null";
        } else {
            bindResourceMessage = MessageFormat.format("'{' appGuid: {0}, route: {1} '}'", br.getAppGuid(), br.getRoute());
        }

        String message = MessageFormat.format("'{' serviceDefinitionId: {0}, planId: {1}, appGuid: {2}, bindResource: {3} '}'", r.getServiceDefinitionId(), r.getPlanId(), r.getAppGuid(), bindResourceMessage);

        return message;
    }

    @Override
    protected void deleteBinding(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan) {
    }

    @Override
    public ServiceInstanceBinding getServiceInstanceBinding(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                    ServiceInstance serviceInstance, Plan plan, ServerAddress host) throws ServiceBrokerException {
        final List<ServerAddress> hosts = serviceInstance.getHosts();
        final ClientMode clientMode = getClientModeOrDefault(serviceInstanceBindingRequest);
        final String serverAddressFilter = clientModeToServerAddressFilter(clientMode, plan);

        final Map<String, Object> credentials = new HashMap<>();

        final String endpoint;
        if (host != null) {
            // If explicit server address should be used for connection string (host != null), use this for endpoint.
            endpoint = host.getIp() + ":" + host.getPort();

            credentials.put("host", endpoint);
        } else {
            endpoint = ServiceInstanceUtils.connectionUrl(ServiceInstanceUtils.filteredServerAddress(hosts, serverAddressFilter));

            final List<String> hostsAsString = hosts.stream()
                    .map(h -> h.getIp() + ":" + h.getPort())
                    .collect(Collectors.toList());
            credentials.put("hosts", hostsAsString);
        }

        final String protocolMode, userCredentials;
        // TODO refactor this conditional!
        final Object xPackProperty = plan.getMetadata().getCustomParameters().get(PROPERTIES_ELASTICSEARCH_X_PACK_ENABLED);
        if (xPackProperty != null && xPackProperty.toString().equals("true")) {
            protocolMode = HTTPS;

            final String username = bindingId;

            final String adminUserName = SUPER_ADMIN;
            final String adminPassword = extractUserPassword(serviceInstance, adminUserName);

            final String userCreationUri = generateUserCreationUri(endpoint, protocolMode, adminUserName, adminPassword);

            final String password = generatePassword();

            addUserToElasticsearch(bindingId, userCreationUri, password);

            credentials.put("username", username);
            credentials.put("password", password);

            userCredentials = String.format("%s:%s@", username, password);
        } else {
            protocolMode = HTTP;

            userCredentials = "";
        }

        final String dbURL = String.format("%s://%s%s", protocolMode, userCredentials, endpoint);
        credentials.put(URI, dbURL);

        return credentials;
    }

    private void addUserToElasticsearch(String bindingId, String userCreationUri, String password) throws ServiceBrokerException {
        final ElasticsearchUser user = new ElasticsearchUser(password, MANAGER_ROLE);

        try {
            ResponseEntity<?> entity = restTemplate.postForEntity(userCreationUri, user, Object.class);

            final HttpStatus statusCode = entity.getStatusCode();
            if (!statusCode.is2xxSuccessful()) {
                throw new ServiceBrokerException(
                        new ServiceInstanceBindingException(bindingId, statusCode, "Cannot create user for binding."));
            }
        } catch (RestClientException e) {
            throw new ServiceBrokerException("Cannot create user for binding.");
        }
    }

    private String generatePassword() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private String extractUserPassword(ServiceInstance serviceInstance, String userName) {
        return serviceInstance
                        .getUsers().stream()
                        .filter(u -> u.getUsername() == userName)
                        .map(User::getPassword)
                        .findFirst().orElse("");
    }

    private String generateUserCreationUri(String endpoint, String protocolMode, String adminUserName, String adminPassword) {
        final String adminUri = String.format("%s://%s:%s@%s", protocolMode, adminUserName, adminPassword, endpoint);
        return String.format(X_PACK_USERS_URI_PATTERN, adminUri);
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

    protected enum ClientMode {
        EGRESS("egress"), INGRESS("ingress");

        public static final String CLIENT_MODE_IDENTIFIER = "clientMode";

        private final String identifier;

        ClientMode(String identifier) {
            this.identifier = identifier;
        }
    }
}
