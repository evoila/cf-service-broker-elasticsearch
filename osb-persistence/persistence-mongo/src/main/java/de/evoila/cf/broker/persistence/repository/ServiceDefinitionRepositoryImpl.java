/**
 * 
 */
package de.evoila.cf.broker.persistence.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Repository
public class ServiceDefinitionRepositoryImpl implements ServiceDefinitionRepository {

	@Autowired
	private Catalog catalog;

	// Depl
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#
	 * getServiceDefinition()
	 */
	@Override
	public List<ServiceDefinition> getServiceDefinition() {
		return catalog.getServices();
	}

	// public Map<String, ServiceInstance> getServiceInstances() {
	// return serviceInstances;
	// }

	// Depl
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#
	 * validateServiceId(java.lang.String)
	 */
	@Override
	public void validateServiceId(String serviceDefinitionId) throws ServiceDefinitionDoesNotExistException {
		for(ServiceDefinition serviceDefinition : catalog.getServices()) {
			if (!serviceDefinitionId.equals(serviceDefinition.getId())) {
				throw new ServiceDefinitionDoesNotExistException(serviceDefinitionId);
			}
		}
	}

	// Depl + Bind
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#getPlan(java
	 * .lang.String)
	 */
	@Override
	public Plan getPlan(String planId) throws ServiceBrokerException {
		for(ServiceDefinition serviceDefinition : catalog.getServices()) {
			for (Plan currentPlan : serviceDefinition.getPlans()) {
				if (currentPlan.getId().equals(planId)) {
					return currentPlan;
				}
			}
		}
		throw new ServiceBrokerException("Missing plan for id: " + planId);
	}

}
