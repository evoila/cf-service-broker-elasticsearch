package de.evoila.cf.broker.repository;

import java.util.List;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceDefinition;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface ServiceDefinitionRepository {

	// Depl
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#
	 * getServiceDefinition()
	 */
	List<ServiceDefinition> getServiceDefinition();

	// Depl
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#
	 * validateServiceId(java.lang.String)
	 */
	void validateServiceId(String serviceDefinitionId) throws ServiceDefinitionDoesNotExistException;

	// Depl + Bind
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#getPlan(java
	 * .lang.String)
	 */
	Plan getPlan(String planId) throws ServiceBrokerException;

}