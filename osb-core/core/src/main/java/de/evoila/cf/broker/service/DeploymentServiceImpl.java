/**
 * 
 */
package de.evoila.cf.broker.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.JobProgressResponse;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceResponse;
import de.evoila.cf.broker.repository.JobRepository;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.AsyncDeploymentService;
import de.evoila.cf.broker.service.DeploymentService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.cpi.custom.props.DomainBasedCustomPropertyHandler;

/**
 * @author Christian Brinker.
 *
 */
@Service
public class DeploymentServiceImpl implements DeploymentService {

	@Autowired
	private DomainBasedCustomPropertyHandler domainPropertyHandler;

	@Autowired
	private PlatformRepository platformRepository;

	@Autowired
	ServiceDefinitionRepository serviceDefinitionRepository;

	@Autowired
	private ServiceInstanceRepository serviceInstanceRepository;
	
	@Autowired
	private JobRepository jobRepository;

	@Autowired(required = false)
	private AsyncDeploymentService asyncDeploymentService;

	@Resource(name = "customProperties")
	public Map<String, String> customProperties;
	
	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public JobProgressResponse getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
		JobProgress progress = asyncDeploymentService.getProgress(serviceInstanceId);

		if (progress == null || !serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceDoesNotExistException("Service instance not found " + serviceInstanceId);
		}

		return new JobProgressResponse(progress);
	}


	@Override
	public ServiceInstanceResponse createServiceInstance(String serviceInstanceId, String serviceDefinitionId,
			String planId, String organizationGuid, String spaceGuid, Map<String, String> parameters,
			Map<String, String> context)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException {


		serviceDefinitionRepository.validateServiceId(serviceDefinitionId);

		if (serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, serviceDefinitionId);
		}

		ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceId, serviceDefinitionId,
				planId, organizationGuid, spaceGuid,
				parameters == null ? new HashMap<String, String>()
						: new HashMap<String, String>(parameters),
				context == null ? new HashMap<String, String>() 
						: new HashMap<String, String>(context));

		Plan plan = serviceDefinitionRepository.getPlan(planId);

		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if(platformService == null) {
			throw new ServiceDefinitionDoesNotExistException(planId);
		}
		
		if (platformService.isSyncPossibleOnCreate(plan)) {
			return syncCreateInstance(serviceInstance, parameters, plan, platformService);
		} else {
			ServiceInstanceResponse serviceInstanceResponse = new ServiceInstanceResponse(serviceInstance, true);

			serviceInstanceRepository.addServiceInstance(serviceInstance.getId(), serviceInstance);

			asyncDeploymentService.asyncCreateInstance(this, serviceInstance, parameters, plan, platformService);

			return serviceInstanceResponse;
		}
	}

	public ServiceInstanceResponse syncCreateInstance (ServiceInstance serviceInstance, Map<String, String> parameters,
                                                       Plan plan, PlatformService platformService) throws ServiceBrokerException {
		ServiceInstance createdServiceInstance;
		try {
			Map<String, String> mergedProperties = domainPropertyHandler.addDomainBasedCustomProperties(plan,
					customProperties, serviceInstance);

			if (parameters != null) {
				for (Entry<String, String> entry : parameters.entrySet()) {
					mergedProperties.putIfAbsent(entry.getKey(), entry.getValue());
				}
			}

			createdServiceInstance = platformService.createInstance(serviceInstance, plan, mergedProperties);
		} catch (PlatformException e) {
			serviceInstanceRepository.deleteServiceInstance(serviceInstance.getId());

			throw new ServiceBrokerException("Could not create instance due to: ", e);
		}

		if (createdServiceInstance.getInternalId() != null)
			serviceInstanceRepository.addServiceInstance(createdServiceInstance.getId(), createdServiceInstance);
		else {
			serviceInstanceRepository.deleteServiceInstance(serviceInstance.getId());

			throw new ServiceBrokerException(
					"Internal error. Service instance was not created. ID was: " + serviceInstance.getId());
		}

		try {
			createdServiceInstance = platformService.postProvisioning(createdServiceInstance, plan);
		} catch (PlatformException e) {
			throw new ServiceBrokerException("Error during service availability verification", e);
		}

		return new ServiceInstanceResponse(createdServiceInstance, false);
	}

	/**
	 * 
	 * @param instanceId
	 * @return
	 */
	protected String getInternalId(String instanceId) {
		return serviceInstanceRepository.getServiceInstance(instanceId).getInternalId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#deleteServiceInstance(
	 * java.lang.String)
	 */
	@Override
	public void deleteServiceInstance(String instanceId)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);

		if (serviceInstance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getPlanId());
		
		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if (platformService.isSyncPossibleOnDelete(serviceInstance)
				&& platformService.isSyncPossibleOnDelete(serviceInstance)) {
			syncDeleteInstance(serviceInstance, platformService);
		} else {
			asyncDeploymentService.asyncDeleteInstance(this, instanceId, serviceInstance, platformService);
		}

	}

	public void syncDeleteInstance (ServiceInstance serviceInstance, PlatformService platformService)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		platformService.preDeprovisionServiceInstance(serviceInstance);

		try {
			platformService.deleteServiceInstance(serviceInstance);
		} catch (PlatformException e) {
			throw new ServiceBrokerException("Error during deletion of service", e);
		}

		serviceInstanceRepository.deleteServiceInstance(serviceInstance.getId());
		jobRepository.deleteJobProgress(serviceInstance.getId());
	}
}
