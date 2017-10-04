/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.evoila.cf.broker.exception.ServerviceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.ServiceInstanceCreationResult;
import de.evoila.cf.broker.service.ServiceInstanceService;

/**
 * @author Christian
 *
 */
public abstract class ServiceInstanceServiceImpl implements ServiceInstanceService {

	public abstract ServiceInstanceCreationResult provisionServiceInstance(String serviceInstanceId, String planId)
			throws ServiceBrokerException;

	public abstract ServiceInstanceBindingResponse bindService(String internalId) throws ServiceBrokerException;

	public abstract void deleteBinding(String internalId) throws ServiceBrokerException;

	
	private Map<String, String> internalIdMapping = new ConcurrentHashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#createServiceInstance(
	 * de.evoila.cf.broker.model.ServiceDefinition, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public String createServiceInstance(ServiceDefinition service, String serviceInstanceId, String planId,
			String organizationGuid, String spaceGuid) throws ServiceInstanceExistsException, ServiceBrokerException {

		if (internalIdMapping.containsKey(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, service.getId());
		}
		// create
		ServiceInstanceCreationResult creationResult = provisionServiceInstance(serviceInstanceId, planId);

		internalIdMapping.put(serviceInstanceId, creationResult.getInternalId());

		return creationResult.getDaschboardUrl();
	}
	
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * de.evoila.cf.broker.service.ServiceInstanceService#getServiceInstance(
	// * java.lang.String)
	// */
	// public ServiceInstance getServiceInstance(String id) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#deleteServiceInstance(
	 * java.lang.String)
	 */
	public ServiceInstance deleteServiceInstance(String id) throws ServiceBrokerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#getInternalId(java.
	 * lang.String)
	 */
	public String getInternalId(String instanceId) {
		return this.internalIdMapping.get(instanceId);
	}
	
	private ConcurrentHashMap<String, String> internalBindingIdMapping = new ConcurrentHashMap<String, String>();

	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			String serviceId, String planId, String appGuid) throws ServiceInstanceBindingExistsException,
					ServiceBrokerException, ServiceInstanceDoesNotExistException {
		String internalId = this.getInternalId(instanceId);
		if (internalId == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		if (internalBindingIdMapping.containsKey(bindingId)) {
			throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
		}

		ServiceInstanceBindingResponse response = bindService(internalId);

		internalBindingIdMapping.put(bindingId, internalId);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.ServiceInstanceBindingService#
	 * deleteServiceInstanceBinding(java.lang.String)
	 */
	public void deleteServiceInstanceBinding(String bindingId)
			throws ServiceBrokerException, ServerviceInstanceBindingDoesNotExistsException {
		String internalId = internalBindingIdMapping.get(bindingId);
		if (internalId == null) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
		}
		deleteBinding(internalId);
	}

}
