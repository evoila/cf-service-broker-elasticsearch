package de.evoila.cf.broker.backup;

import de.evoila.cf.broker.bean.BackupConfiguration;
import de.evoila.cf.broker.elasticsearch.connector.ElasticsearchConnector;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BackupCustomService;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.custom.constants.CredentialConstants;
import de.evoila.cf.security.credentials.CredentialStore;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Hahn, René Schollmeyer
 */

@Service
@ConditionalOnBean(BackupConfiguration.class)
public class BackupCustomServiceImpl implements BackupCustomService {
    private static final Logger log = LoggerFactory.getLogger(BackupCustomServiceImpl.class);

    private ServiceInstanceRepository serviceInstanceRepository;

    private CatalogService catalogService;

    private CredentialStore credentialStore;

    private ElasticsearchConnector elasticsearchConnector;

    public BackupCustomServiceImpl(ServiceInstanceRepository serviceInstanceRepository, CatalogService catalogService,
                                   CredentialStore credentialStore, ElasticsearchConnector elasticsearchConnector) {
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.catalogService = catalogService;
        this.credentialStore = credentialStore;
        this.elasticsearchConnector = elasticsearchConnector;
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

        RestHighLevelClient client = elasticsearchConnector.createElasticClient(serviceInstance.getHosts(), serviceInstanceId, credentialsProvider);

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
}
