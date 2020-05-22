package de.evoila.cf.broker.backup;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.elasticsearch.connector.ElasticsearchConnector;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.custom.model.Snapshot;
import de.evoila.cf.broker.service.custom.model.SnapshotPolicy;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.AcknowledgedResponse;
import org.elasticsearch.client.slm.*;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author René Schollmeyer
 **/
@RestController
@RequestMapping(value = "/custom/v2/manage/service_instances")
public class BackupCustomController extends BaseController {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private ElasticsearchConnector elasticsearchConnector;

    private ServiceInstanceRepository serviceInstanceRepository;

    public BackupCustomController(ElasticsearchConnector elasticsearchConnector, ServiceInstanceRepository serviceInstanceRepository) {
        this.elasticsearchConnector = elasticsearchConnector;
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    @PutMapping(value = "/{serviceInstanceId}/snapshots/policy/{policyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createPolicy(@PathVariable("serviceInstanceId") String serviceInstanceId,
                                       @PathVariable("policyId") String policyId,
                                       @RequestBody SnapshotPolicy snapshotPolicy) throws ServiceInstanceDoesNotExistException, IOException {

        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        Map<String, Object> config = new HashMap<>();
        config.put("indices", snapshotPolicy.getConfig().getIndices());

        TimeValue timeValue;
        if(snapshotPolicy.getRetention().getTimeValue().toLowerCase().equals("hours")) {
            timeValue = TimeValue.timeValueHours(snapshotPolicy.getRetention().getExpireAfter());
        } else {
            timeValue = TimeValue.timeValueDays(snapshotPolicy.getRetention().getExpireAfter());
        }

        SnapshotRetentionConfiguration retention = new SnapshotRetentionConfiguration(timeValue,
                snapshotPolicy.getRetention().getMinCount(),
                snapshotPolicy.getRetention().getMaxCount());

        SnapshotLifecyclePolicy policy = new SnapshotLifecyclePolicy(policyId, snapshotPolicy.getName(),
                snapshotPolicy.getSchedule(), snapshotPolicy.getRepository(), config, retention);

        PutSnapshotLifecyclePolicyRequest request = new PutSnapshotLifecyclePolicyRequest(policy);

        RestHighLevelClient client = elasticsearchConnector.createElasticClient(serviceInstance.getHosts(), serviceInstanceId);

        AcknowledgedResponse response = null;
        if(client != null) {
            response = client.indexLifecycle().putSnapshotLifecyclePolicy(request, RequestOptions.DEFAULT);
            client.close();
        } else {
            log.error("Client creation failed on all available hosts.");
        }

        if(response.isAcknowledged()) {
            return new ResponseEntity(String.format("{ \"acknowledged\" : %b }", response.isAcknowledged()), HttpStatus.OK);
        } else {
            return new ResponseEntity(String.format("{ \"acknowledged\" : %b }", response.isAcknowledged()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{serviceInstanceId}/snapshots/policy/{policyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deletePolicy(@PathVariable("serviceInstanceId") String serviceInstanceId,
                                       @PathVariable("policyId") String policyId) throws ServiceInstanceDoesNotExistException, IOException {

        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        DeleteSnapshotLifecyclePolicyRequest request = new DeleteSnapshotLifecyclePolicyRequest(policyId);

        RestHighLevelClient client = elasticsearchConnector.createElasticClient(serviceInstance.getHosts(), serviceInstanceId);

        AcknowledgedResponse response = null;
        if(client != null) {
            response = client.indexLifecycle().deleteSnapshotLifecyclePolicy(request, RequestOptions.DEFAULT);
            client.close();
        } else {
            log.error("Client creation failed on all available hosts.");
        }

        if(response.isAcknowledged()) {
            return new ResponseEntity(String.format("{ \"acknowledged\" : %b }", response.isAcknowledged()), HttpStatus.OK);
        } else {
            return new ResponseEntity(String.format("{ \"acknowledged\" : %b }", response.isAcknowledged()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{serviceInstanceId}/snapshots/policy/{policyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getPolicy(@PathVariable("serviceInstanceId") String serviceInstanceId,
                                    @PathVariable("policyId") String policyId) throws ServiceInstanceDoesNotExistException, IOException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        GetSnapshotLifecyclePolicyRequest request = new GetSnapshotLifecyclePolicyRequest();

        RestHighLevelClient client = elasticsearchConnector.createElasticClient(serviceInstance.getHosts(), serviceInstanceId);

        GetSnapshotLifecyclePolicyResponse response = null;
        if(client != null) {
            response = client.indexLifecycle().getSnapshotLifecyclePolicy(request, RequestOptions.DEFAULT);
            client.close();
        } else {
            log.error("Client creation failed on all available hosts.");
        }

        if(response != null) {
            return new ResponseEntity(response.getPolicies().get(policyId), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{serviceInstanceId}/snapshots/policy/{policyId}/execute", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity executePolicy(@PathVariable("serviceInstanceId") String serviceInstanceId,
                                                    @PathVariable("policyId") String policyId) throws ServiceInstanceDoesNotExistException, IOException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        ExecuteSnapshotLifecyclePolicyRequest request = new ExecuteSnapshotLifecyclePolicyRequest(policyId);

        RestHighLevelClient client = elasticsearchConnector.createElasticClient(serviceInstance.getHosts(), serviceInstanceId);

        ExecuteSnapshotLifecyclePolicyResponse response = null;
        if(client != null) {
            response = client.indexLifecycle().executeSnapshotLifecyclePolicy(request, RequestOptions.DEFAULT);
            client.close();
        } else {
            log.error("Client creation failed on all available hosts.");
        }

        if(response != null) {
            return new ResponseEntity(String.format("{ \"snapshot_name\" : \"%s\" }", response.getSnapshotName()), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{serviceInstanceId}/snapshots/{repositoryName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getSnapshots(@PathVariable("serviceInstanceId") String serviceInstanceId,
                                       @PathVariable("repositoryName") String repositoryName) throws ServiceInstanceDoesNotExistException, IOException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        GetSnapshotsRequest request = new GetSnapshotsRequest();
        request.repository(repositoryName);
        request.verbose(true);

        RestHighLevelClient client = elasticsearchConnector.createElasticClient(serviceInstance.getHosts(), serviceInstanceId);

        GetSnapshotsResponse response = null;
        if(client != null) {
            response = client.snapshot().get(request, RequestOptions.DEFAULT);
            client.close();
        } else {
            log.error("Client creation failed on all available hosts.");
        }

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy - HH:mm");

        List<Snapshot> snapshots = new ArrayList<>();
        response.getSnapshots().forEach(snapshotInfo -> {
            Snapshot snapshot = new Snapshot(snapshotInfo.snapshotId().getName(),
                    format.format(new Date(snapshotInfo.startTime())),
                    snapshotInfo.state().toString(), snapshotInfo.indices());
            snapshots.add(snapshot);
        });

        if(response != null) {
            return new ResponseEntity(snapshots, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
