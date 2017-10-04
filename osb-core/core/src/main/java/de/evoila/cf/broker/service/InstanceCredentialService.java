package de.evoila.cf.broker.service;


import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.model.DatabaseCredential;

public interface InstanceCredentialService {
    DatabaseCredential getCredentialsForInstanceId (String serviceInstanceId) throws ServiceInstanceDoesNotExistException;
}
