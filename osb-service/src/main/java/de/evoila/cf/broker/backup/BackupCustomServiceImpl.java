package de.evoila.cf.broker.backup;

import de.evoila.cf.broker.bean.BackupConfiguration;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BackupCustomService;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.custom.ElasticsearchUtilities;
import de.evoila.cf.broker.service.custom.constants.CredentialConstants;
import de.evoila.cf.security.credentials.CredentialStore;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private static final Logger log = LoggerFactory.getLogger(BackupCustomServiceImpl.class);

    private ServiceInstanceRepository serviceInstanceRepository;

    private CatalogService catalogService;

    private CredentialStore credentialStore;

    public BackupCustomServiceImpl(ServiceInstanceRepository serviceInstanceRepository, CatalogService catalogService,
                                   CredentialStore credentialStore) {
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.catalogService = catalogService;
        this.credentialStore = credentialStore;
    }

    @Override
    public Map<String, String> getItems(String serviceInstanceId) throws ServiceInstanceDoesNotExistException {
        validateServiceInstanceId(serviceInstanceId);
        final HashMap<String, String> map = new HashMap<>();

        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        String username = CredentialConstants.SUPER_ADMIN;
        String password = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getPassword();

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        RestHighLevelClient client = createElasticClient(serviceInstance.getHosts(), serviceInstanceId, credentialsProvider);

        if (client != null) {
            String[] indices;

            try {
                GetIndexRequest request = new GetIndexRequest("*");

                indices = client.indices().get(request, RequestOptions.DEFAULT).getIndices();

                for(String index : indices)
                    map.put(index, index);

                client.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.error("Client creation failed on all available hosts.");
        }

        return map;
    }

    private ServiceInstance validateServiceInstanceId(String serviceInstanceId) throws ServiceInstanceDoesNotExistException {
        ServiceInstance instance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        if(instance == null || instance.getHosts().size() <= 0) {
            log.error("Service instance " + serviceInstanceId + " does not exist!");
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        return instance;
    }

    private boolean isXpackEnabled(String serviceInstanceId) throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
        String serviceDefinitionId = serviceInstance.getServiceDefinitionId();
        String planId = serviceInstance.getPlanId();

        Plan usedPlan = catalogService.getServiceDefinition(serviceDefinitionId).getPlans().stream().filter(
                plan -> plan.getId().equals(planId)
        ).findFirst().orElse(null);

        return ElasticsearchUtilities.planContainsXPack(usedPlan);
    }

    private RestHighLevelClient createElasticClient(List<ServerAddress> hosts, String serviceInstanceId, CredentialsProvider credentialsProvider) {
        boolean success = false;

        for(ServerAddress serverAddress : hosts) {
            String ip = serverAddress.getIp();

            RestHighLevelClient client = null;
            
            try {
                client = new RestHighLevelClient(
                        RestClient.builder(new HttpHost(ip, 9200, ((isXpackEnabled(serviceInstanceId)) ? HTTPS : HTTP)))
                                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                );

                client.ping(RequestOptions.DEFAULT);
                success = true;
            } catch (IOException | ServiceInstanceDoesNotExistException | ServiceDefinitionDoesNotExistException e) {
                log.error(String.format("Failed to create client on host %s: %s", ip, e.getMessage()));
            }

            if(success) {
                return client;
            }
        }

        return null;
    }
}
