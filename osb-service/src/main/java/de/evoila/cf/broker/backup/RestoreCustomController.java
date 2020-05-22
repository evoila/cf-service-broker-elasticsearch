package de.evoila.cf.broker.backup;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.elasticsearch.connector.ElasticsearchConnector;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/custom/v2/manage/service_instances")
public class RestoreCustomController extends BaseController  {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private ElasticsearchConnector elasticsearchConnector;

    private ServiceInstanceRepository serviceInstanceRepository;

    public RestoreCustomController(ElasticsearchConnector elasticsearchConnector, ServiceInstanceRepository serviceInstanceRepository) {
        this.elasticsearchConnector = elasticsearchConnector;
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    @PostMapping(value = "/{serviceInstanceId}/repository/{repositoryName}/snapshots/{snapshotId}/restore", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity restoreSnapshot(@PathVariable("serviceInstanceId") String serviceInstanceId,
                                          @PathVariable("repositoryName") String repositoryName,
                                          @PathVariable("snapshotId") String snapshotId) throws ServiceInstanceDoesNotExistException, IOException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        RestoreSnapshotRequest request = new RestoreSnapshotRequest(repositoryName, snapshotId);

        RestHighLevelClient client = elasticsearchConnector.createElasticClient(serviceInstance.getHosts(), serviceInstanceId);

        RestoreSnapshotResponse response = null;
        if(client != null) {
            response = client.snapshot().restore(request, RequestOptions.DEFAULT);
            client.close();
        } else {
            log.error("Client creation failed on all available hosts.");
        }

        if(response != null) {
            return new ResponseEntity(response.getRestoreInfo(), HttpStatus.valueOf(response.status().getStatus()));
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
