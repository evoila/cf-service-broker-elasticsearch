package de.evoila.cf.broker.elasticsearch.connector;

import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.custom.ElasticsearchUtilities;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ElasticsearchConnector {
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchConnector.class);

    private ServiceInstanceRepository serviceInstanceRepository;

    private CatalogService catalogService;

    public ElasticsearchConnector(ServiceInstanceRepository serviceInstanceRepository, CatalogService catalogService) {
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.catalogService = catalogService;
    }

    public RestHighLevelClient createElasticClient(List<ServerAddress> hosts, String serviceInstanceId, CredentialsProvider credentialsProvider) {
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

    private boolean isXpackEnabled(String serviceInstanceId) throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
        String serviceDefinitionId = serviceInstance.getServiceDefinitionId();
        String planId = serviceInstance.getPlanId();

        Plan usedPlan = catalogService.getServiceDefinition(serviceDefinitionId).getPlans().stream().filter(
                plan -> plan.getId().equals(planId)
        ).findFirst().orElse(null);

        return ElasticsearchUtilities.planContainsXPack(usedPlan);
    }
}
