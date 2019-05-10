package de.evoila.cf.broker.backup;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.bean.BackupConfiguration;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.User;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BackupCustomService;
import de.evoila.cf.broker.service.custom.ElasticsearchBindingService;
import de.evoila.cf.broker.service.custom.model.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Hahn
 */

@Service
@ConditionalOnBean(BackupConfiguration.class)
public class BackupCustomServiceImpl implements BackupCustomService {
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    private static final String INDEX_URI_PATTERN = "%s/_cat/indices?format=json&pretty";
    private static final String PROPERTIES_HTTPS_ENABLED = "elasticsearch.xpack.security.http.ssl.enabled";
    private static final String PROPERTIES_X_PACK_ENABLED = "elasticsearch.xpack.security.enabled";
    private static final Logger log = LoggerFactory.getLogger(BackupCustomServiceImpl.class);

    private ServiceInstanceRepository serviceInstanceRepository;
    private ServiceDefinitionRepository serviceDefinitionRepository;

    public BackupCustomServiceImpl(ServiceInstanceRepository serviceInstanceRepository, ServiceDefinitionRepository serviceDefinitionRepository) {
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
    }

    @Override
    public Map<String, String> getItems(String serviceInstanceId) throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
        final ServiceInstance serviceInstance = this.validateServiceInstanceId(serviceInstanceId);
        final HashMap<String, String> map = new HashMap<>();
        final Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getPlanId());

        final List<ServerAddress> hosts = serviceInstance.getHosts();


        final String protocolMode;
        final RestTemplate restTemplate = new RestTemplate();

        if(planContainsXPack(plan)) {
            if (isHttpsEnabled(plan)) {
                protocolMode = HTTPS;
            } else {
                protocolMode = HTTP;
            }

            final String username = ElasticsearchBindingService.SUPER_ADMIN;
            final String password = extractUserPassword(serviceInstance, username);
            final BasicAuthorizationInterceptor basicAuthorizationInterceptor = new BasicAuthorizationInterceptor(username, password);


            restTemplate.getInterceptors().add(basicAuthorizationInterceptor);

            return getIndexMap(hosts, protocolMode, restTemplate);
        } else {
            protocolMode = HTTP;

            return getIndexMap(hosts, protocolMode, restTemplate);
        }
    }

    private HashMap<String, String> getIndexMap(List<ServerAddress> hosts, String protocolMode, RestTemplate restTemplate) {
        final HashMap<String, String> map = new HashMap<>();
        final ObjectMapper mapper = new ObjectMapper();

        for(ServerAddress serverAddress : hosts) {
            String endpoint = serverAddress.getIp() + ":" + serverAddress.getPort();
            String uri = generateIndexUri(endpoint, protocolMode);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            if(response.getStatusCode() == HttpStatus.OK) {
                try {
                    List<Index> indexList = mapper.readValue(response.getBody(), new TypeReference<List<Index>>(){});

                    for(Index index : indexList) {
                        map.put(index.getName(), index.getName());
                    }
                } catch (IOException e) {
                    Log.error("Could not parse index information");
                }
            }
        }
        return map;
    }

    private ServiceInstance validateServiceInstanceId(String serviceInstanceId) throws ServiceInstanceDoesNotExistException {
        ServiceInstance instance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        if(instance == null || instance.getHosts().size() <= 0) {
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        return instance;
    }

    private String extractUserPassword(ServiceInstance serviceInstance, String userName) {
        return serviceInstance
                .getUsers().stream()
                .filter(u -> u.getUsername().equals(userName))
                .map(User::getPassword)
                .findFirst().orElse("");
    }

    private boolean planContainsXPack(Plan plan) {
        Object XPackProperyRaw;
        try {
            XPackProperyRaw = extractProperty(plan.getMetadata().getProperties(), PROPERTIES_X_PACK_ENABLED);
        } catch (IllegalArgumentException e) {
            log.error("Property " + PROPERTIES_X_PACK_ENABLED + " is missing for plan " + plan.getName(), e);
            return false;
        }

        return XPackProperyRaw instanceof  Boolean && ((Boolean) XPackProperyRaw);
    }

    private boolean isHttpsEnabled(Plan plan) {
        Object pluginsRaw;
        try {
            pluginsRaw = extractProperty(plan.getMetadata().getProperties(), PROPERTIES_HTTPS_ENABLED);
        } catch (IllegalArgumentException e) {
            log.error("Property " + PROPERTIES_HTTPS_ENABLED + " is missing for plan " + plan.getName(), e);
            return false;
        }

        return pluginsRaw instanceof Boolean && (Boolean) pluginsRaw;
    }

    private Object extractProperty(Object elasticsearchPropertiesRaw, List<String> keyElements) {
        if (elasticsearchPropertiesRaw instanceof Map) {
            final Map<String, Object> elasticsearchProperties = ((Map<String, Object>) elasticsearchPropertiesRaw);

            final Object pluginPropertiesRaw = elasticsearchProperties.get(keyElements.get(0));


            if (keyElements.size() == 1) {
                return pluginPropertiesRaw;
            } else {

                if (pluginPropertiesRaw == null) {
                    throw new IllegalArgumentException("Could not access property. Intermediate map is missing.");
                }

                final List<String> nextStepSelectors = keyElements.subList(1, keyElements.size());
                return extractProperty(pluginPropertiesRaw, nextStepSelectors);
            }
        }
        throw new IllegalArgumentException("Wrong parameter format. Argument was not of type Map.");
    }

    private Object extractProperty(Object elasticsearchPropertiesRaw, String key) {
        final List<String> keyElements = Arrays.asList(key.split("\\."));

        return extractProperty(elasticsearchPropertiesRaw, keyElements);
    }

    private String generateIndexUri(String endpoint, String protocolMode) {
        final String endpointUri = String.format("%s://%s", protocolMode, endpoint);
        return String.format(INDEX_URI_PATTERN, endpointUri);
    }
}
