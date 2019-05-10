package de.evoila.cf.broker.backup;

import de.evoila.cf.broker.bean.BackupConfiguration;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BackupCustomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    public BackupCustomServiceImpl(ServiceInstanceRepository serviceInstanceRepository) {
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    @Override
    public Map<String, String> getItems(String serviceInstanceId) throws ServiceInstanceDoesNotExistException {
        validateServiceInstanceId(serviceInstanceId);
        final HashMap<String, String> map = new HashMap<>();
        map.put("elasticsearch_cluster", "elasticsearch_cluster");
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
