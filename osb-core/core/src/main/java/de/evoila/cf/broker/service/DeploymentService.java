/**
 * 
 */
package de.evoila.cf.broker.service;

import java.util.Map;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.ServiceInstanceResponse;
import de.evoila.cf.broker.model.JobProgressResponse;

/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract interface DeploymentService {
	
	/**
	 * @return All known ServiceInstances
	 */
	// List<ServiceInstance> getAllServiceInstances();
	/**
	 * @param id
	 * @return The ServiceInstance with the given id or null if one does not
	 *         exist
	 */
	// ServiceInstance getServiceInstance(String id);
	
	/**
	 * 
	 * @param serviceInstanceId
	 * @return
	 * @throws ServiceInstanceDoesNotExistException
	 * @throws ServiceBrokerException
	 */
	JobProgressResponse getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException;
	
	/**
	 * @param instance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 */
	public ServiceInstanceResponse createServiceInstance(String serviceInstanceId, String serviceDefinitionId,
			String planId, String organizationGuid, String spaceGuid, Map<String, String> parameters,
			Map<String, String> context)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException;

	/**
	 * @param instance
	 * @throws ServiceInstanceDoesNotExistException 
	 * @throws ServiceBrokerException 
	 */
	public void deleteServiceInstance(String instanceId) throws ServiceBrokerException, ServiceInstanceDoesNotExistException;

}
