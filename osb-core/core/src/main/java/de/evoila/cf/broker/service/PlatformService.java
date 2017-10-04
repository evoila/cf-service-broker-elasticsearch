/**
 * 
 */
package de.evoila.cf.broker.service;

import java.util.Map;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract interface PlatformService {

	/**
	 * 
	 */
	public void registerCustomPlatformServie();
	
	/**
	 * @param plan
	 * @return
	 */
	public boolean isSyncPossibleOnCreate(Plan plan);

	/**
	 * @param plan
	 * @return
	 */
	public boolean isSyncPossibleOnDelete(ServiceInstance instance);

	/**
	 * @param plan
	 * @return
	 */
	public boolean isSyncPossibleOnUpdate(ServiceInstance instance, Plan plan);
	
	/**
	 * 
	 * @param serviceInstance
	 * @param plan
	 * @return
	 * @throws ServiceBrokerException
	 * @throws PlatformException 
	 */
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan)
			throws PlatformException;
	
	/**
	 * 
	 * @param serviceInstance
	 */
	public void preDeprovisionServiceInstance(ServiceInstance serviceInstance);
	
	/**
	 * @param instance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 * @throws Exception 
	 */
	public ServiceInstance createInstance(ServiceInstance instance, Plan plan, Map<String, String> customParameters) throws PlatformException;

	/**
	 * Same result as in PlatformService.createInstance(), but without creating
	 * a ServiceInstance on the platform. Used to provide information during
	 * asynchronous operations
	 * 
	 * @param instance
	 * @param plan
	 * @return
	 */
	public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan);

	/**
	 * @param instance
	 * @throws ServiceInstanceDoesNotExistException 
	 * @throws ServiceBrokerException 
	 */
	public void deleteServiceInstance(ServiceInstance serviceInstance) throws PlatformException;

	/**
	 * @param instance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 */
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan);

}
