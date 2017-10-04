/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.RouteBindingRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.HAProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 *
 */
public abstract class BindingServiceImpl implements BindingService {

	private final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);

	@Autowired
	protected BindingRepository bindingRepository;

	@Autowired
	protected ServiceDefinitionRepository serviceDefinitionRepository;

	@Autowired
	protected ServiceInstanceRepository serviceInstanceRepository;

	@Autowired
	protected RouteBindingRepository routeBindingRepository;

	@Autowired
	protected HAProxyService haProxyService;

	protected abstract void deleteBinding(String bindingId, ServiceInstance serviceInstance)
			throws ServiceBrokerException;

	@Override
	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			String serviceId, String planId, boolean generateServiceKey, String route)
					throws ServiceInstanceBindingExistsException, ServiceBrokerException,
					ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {

		validateBindingNotExists(bindingId, instanceId);

		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);

		if (serviceInstance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = serviceDefinitionRepository.getPlan(planId);

		if (route != null) {
			RouteBinding routeBinding = bindRoute(serviceInstance, route);
			routeBindingRepository.addRouteBinding(routeBinding);
			ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(routeBinding.getRoute());
			return response;
		}

		ServiceInstanceBinding binding;
		if (generateServiceKey) {
			List<ServerAddress> externalServerAddresses = haProxyService.appendAgent(serviceInstance.getHosts(), bindingId, instanceId);

			binding = bindServiceKey(bindingId, serviceInstance, plan, externalServerAddresses);
		} else {
			binding = bindService(bindingId, serviceInstance, plan);
		}

		ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(binding);
		bindingRepository.addInternalBinding(binding);

		return response;
	}

	/**
	 * @param serviceInstance
	 * @param route
	 * @return
	 */
	protected abstract RouteBinding bindRoute(ServiceInstance serviceInstance, String route);

	protected ServiceInstanceBinding createServiceInstanceBinding(String bindingId, String serviceInstanceId,
			Map<String, Object> credentials, String syslogDrainUrl, String appGuid) {
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials,
				syslogDrainUrl);
		return binding;
	}

	@Override
	public void deleteServiceInstanceBinding(String bindingId)
			throws ServiceBrokerException, ServerviceInstanceBindingDoesNotExistsException {
		ServiceInstance serviceInstance = getBinding(bindingId);

		try {
			ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
			List<ServerAddress> externalServerAddresses = binding.getExternalServerAddresses();
			if (externalServerAddresses != null) {
				haProxyService.removeAgent(serviceInstance.getHosts(), bindingId);
			}

			deleteBinding(bindingId, serviceInstance);
		} catch (ServiceBrokerException e) {
			log.error("Could not cleanup service binding", e);
		} finally {
			bindingRepository.deleteBinding(bindingId);
		}
	}

	private void validateBindingNotExists(String bindingId, String instanceId)
			throws ServiceInstanceBindingExistsException {
		if (bindingRepository.containsInternalBindingId(bindingId)) {
			throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
		}
	}

	private ServiceInstance getBinding(String bindingId) throws ServerviceInstanceBindingDoesNotExistsException {
		if (!bindingRepository.containsInternalBindingId(bindingId)) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
		}
		String serviceInstanceId = bindingRepository.getInternalBindingId(bindingId);
		if (serviceInstanceId == null) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
		}
		return serviceInstanceRepository.getServiceInstance(serviceInstanceId);
	}

	/**
	 * @param bindingId
	 * @param serviceInstance
	 * @param plan
	 * @param externalAddresses
	 * @return
	 * @throws ServiceBrokerException
	 */
	protected ServiceInstanceBinding bindServiceKey(String bindingId, ServiceInstance serviceInstance, Plan plan,
			List<ServerAddress> externalAddresses) throws ServiceBrokerException {

		log.debug("bind service key");

		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, externalAddresses.get(0));

		ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(),
				credentials, null);
		serviceInstanceBinding.setExternalServerAddresses(externalAddresses);
		return serviceInstanceBinding;
	}

	/**
	 * @param bindingId
	 * @param serviceInstance
	 * @param plan
	 * @return
	 * @throws ServiceBrokerException
	 */
	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {

		log.debug("bind service");

		ServerAddress host = serviceInstance.getHosts().get(0);
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, host);

		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials, null);
	}

	/**
	 * @param bindingId
	 * @param serviceInstance
	 * @param host
	 * @return
	 * @throws ServiceBrokerException
	 */
	protected abstract Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
			ServerAddress host) throws ServiceBrokerException;

}
