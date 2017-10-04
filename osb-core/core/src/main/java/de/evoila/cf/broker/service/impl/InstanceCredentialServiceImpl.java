package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BackupTypeService;
import de.evoila.cf.broker.service.InstanceCredentialService;
import de.evoila.cf.model.DatabaseCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(BackupTypeService.class)
public class InstanceCredentialServiceImpl implements InstanceCredentialService {
    @Autowired
    BackupTypeService backupTypeService;
    @Autowired
    ServiceInstanceRepository repository;

    @Override
    public DatabaseCredential getCredentialsForInstanceId (String serviceInstanceId) throws ServiceInstanceDoesNotExistException{
        ServiceInstance instance = repository.getServiceInstance(serviceInstanceId);
        if(instance == null || instance.getHosts().size() <= 0){
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }
        DatabaseCredential credential = new DatabaseCredential();
        credential.setContext(serviceInstanceId);
        credential.setUsername(serviceInstanceId);
        credential.setPassword(serviceInstanceId);
        credential.setHostname(instance.getHosts().get(0).getIp());
        credential.setPort(instance.getHosts().get(0).getPort());
        credential.setType(backupTypeService.getType());
        return credential;
    }
}
