/**
 * 
 */
package de.evoila.cf.broker.persistence.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;

/**
 * @author Patrick Weber, evoila.
 *
 */
@Service
public class ServiceInstanceRepositoryImpl
		implements ServiceInstanceRepository {
	
	@Autowired
	de.evoila.cf.broker.persistence.mongodb.repository.ServiceInstanceRepository serviceInstanceRepository;

	@Override
	public ServiceInstance getServiceInstance(String instanceId) {
		return serviceInstanceRepository.findOne(instanceId);
	}

	@Override
	public boolean containsServiceInstanceId(String serviceInstanceId) {
		return serviceInstanceRepository.exists(serviceInstanceId);
	}

	@Override
	public void addServiceInstance(String id, ServiceInstance serviceInstance) {
		if (!id.equals(serviceInstance.getId())) {
			serviceInstance = new ServiceInstance(id, serviceInstance.getServiceDefinitionId(),
					serviceInstance.getPlanId(), serviceInstance.getOrganizationGuid(), serviceInstance.getSpaceGuid(),
					serviceInstance.getParameters(), serviceInstance.getDashboardUrl(),
					serviceInstance.getInternalId());
		}
		serviceInstanceRepository.save(serviceInstance);
	}

	@Override
	public void deleteServiceInstance(String serviceInstanceId) {
		serviceInstanceRepository.delete(serviceInstanceId);
	}

}
