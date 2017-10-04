package de.evoila.cf.broker.service;

import java.util.Map;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * 
 * @author Dennis Mueller, evoila GmbH, Sep 9, 2015
 *
 */

public interface AsyncDeploymentService {

	void asyncCreateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
			Map<String, String> parameters, Plan plan, PlatformService platformService);

	void asyncDeleteInstance(DeploymentServiceImpl deploymentService, String instanceId,
			ServiceInstance serviceInstance, PlatformService platformService)
					throws ServiceInstanceDoesNotExistException;

	JobProgress getProgress(String serviceInstanceId);
}
