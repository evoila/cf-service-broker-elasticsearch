package de.evoila.cf.broker.backup;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.EnvironmentUtils;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.custom.constants.CredentialConstants;
import de.evoila.cf.broker.service.custom.model.SnapshotPolicy;
import de.evoila.cf.cpi.bosh.BoshPlatformService;
import de.evoila.cf.cpi.bosh.ServiceUnavailableException;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.security.credentials.CredentialStore;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author René Schollmeyer
 **/
@RestController
@RequestMapping(value = "/custom/v2/manage/service_instances")
public class BackupCustomController extends BaseController {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private RestTemplate restTemplate;

    private BoshPlatformService platformService;

    private CredentialStore credentialStore;

    private ServiceInstanceRepository serviceInstanceRepository;

    private static String SERVICE_BROKER_PREFIX = "sb-";

    // HTTP too or only HTTPS? Port configurable?
    private static String ELASTIC_SLM_URI = "https://%s:9200/_slm/policy/%s";

    private static String ELASTIC_CAT_SNAPSHOTS_URI = "https://%s:9200/_cat/snapshots/%s";

    public BackupCustomController(RestTemplate restTemplate, BoshPlatformService platformService,
                                  Environment environment, CredentialStore credentialStore,
                                  ServiceInstanceRepository serviceInstanceRepository) {
        this.restTemplate = restTemplate;
        this.platformService = platformService;
        this.credentialStore = credentialStore;
        this.serviceInstanceRepository = serviceInstanceRepository;

        if (EnvironmentUtils.isTestEnvironment(environment)) {
            SERVICE_BROKER_PREFIX += "test-";
        }
    }

    @PutMapping(value = "/{serviceInstanceId}/snapshots/policy")
    public ResponseEntity createPolicy(@PathVariable("serviceInstanceId") String serviceInstanceId,
                                       @RequestBody SnapshotPolicy snapshotPolicy) throws ServiceUnavailableException {

        List<String> hosts = getHosts(serviceInstanceId);

        if(hosts.isEmpty()) {
            throw new ServiceUnavailableException(SERVICE_BROKER_PREFIX+serviceInstanceId);
        }

        String username = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getUsername();
        String password = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getPassword();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", "Basic " + encodeBase64(username, password));

        HttpEntity<SnapshotPolicy> entity = new HttpEntity<>(snapshotPolicy, httpHeaders);


        return restTemplate.exchange(String.format(ELASTIC_SLM_URI, hosts.get(0), serviceInstanceId), HttpMethod.PUT, entity, String.class);
    }

    @DeleteMapping(value = "/{serviceInstanceId}/snapshots/policy")
    public ResponseEntity deletePolicy(@PathVariable("serviceInstanceId") String serviceInstanceId) throws ServiceUnavailableException {

        List<String> hosts = getHosts(serviceInstanceId);

        if(hosts.isEmpty()) {
            throw new ServiceUnavailableException(SERVICE_BROKER_PREFIX+serviceInstanceId);
        }

        String username = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getUsername();
        String password = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getPassword();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodeBase64(username, password));

        HttpEntity entity = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(String.format(ELASTIC_SLM_URI, hosts.get(0), serviceInstanceId), HttpMethod.DELETE, entity, String.class);
    }

    @PostMapping(value = "/{serviceInstanceId}/snapshots/policy/execute")
    public ResponseEntity executePolicy(@PathVariable("serviceInstanceId") String serviceInstanceId) throws ServiceUnavailableException {

        List<String> hosts = getHosts(serviceInstanceId);

        if(hosts.isEmpty()) {
            throw new ServiceUnavailableException(SERVICE_BROKER_PREFIX+serviceInstanceId);
        }

        String username = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getUsername();
        String password = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getPassword();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodeBase64(username, password));

        HttpEntity entity = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(String.format(ELASTIC_SLM_URI+"/_execute", hosts.get(0), serviceInstanceId), HttpMethod.POST, entity, String.class);
    }

    @GetMapping(value = "/{serviceInstanceId}/snapshots")
    public ResponseEntity getSnapshots(@PathVariable("serviceInstanceId") String serviceInstanceId) throws ServiceUnavailableException, ServiceInstanceDoesNotExistException, IOException {
        List<String> hosts = getHosts(serviceInstanceId);

        if(hosts.isEmpty()) {
            throw new ServiceUnavailableException(SERVICE_BROKER_PREFIX+serviceInstanceId);
        }

        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        Manifest manifest = platformService.getDeployedManifest(serviceInstance);

        Map<String, Object> elasticsearch = (Map<String, Object>) manifest.getInstanceGroups().get(0).getProperties().get("elasticsearch");
        Map<String, Object> backup = (Map<String, Object>) elasticsearch.get("backup");
        Map<String, Object> s3 = (Map<String, Object>) backup.get("s3");
        Map<String, Object> client = (Map<String, Object>) s3.get("client");
        Map<String, Object> defaultClient = (Map<String, Object>) client.get("default");
        String bucketName = (String) defaultClient.get("bucket_name");

        String username = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getUsername();
        String password = credentialStore.getUser(serviceInstanceId, CredentialConstants.SUPER_ADMIN).getPassword();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodeBase64(username, password));

        HttpEntity entity = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(String.format(ELASTIC_CAT_SNAPSHOTS_URI, hosts.get(0), bucketName), HttpMethod.GET, entity, String.class);
    }

    private List<String> getHosts(String serviceInstanceId) {
        List<String> hosts = new ArrayList<>();

        platformService.getBoshClient().client().vms().listDetails(SERVICE_BROKER_PREFIX+serviceInstanceId)
                .toBlocking().first().forEach(vm -> {
            if(vm.getJobState().equals("running") && (vm.getJobName().equals("general_nodes") || vm.getJobName().equals("data_nodes"))) {
                vm.getIps().forEach(ip -> hosts.add(ip));
            }
        });

        return hosts;
    }

    private String encodeBase64(String username, String password) {
        String plainCredentials = username + ":" + password;
        byte[] base64Credentials = Base64.encodeBase64(plainCredentials.getBytes());

        return new String(base64Credentials);
    }
}
