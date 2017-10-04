package de.evoila.cf.broker.service.impl;

import java.util.Map;

import de.evoila.cf.broker.service.DeploymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.AsyncDeploymentService;
import de.evoila.cf.broker.service.JobProgressService;
import de.evoila.cf.broker.service.PlatformService;

@Service
public class AsyncDeploymentServiceImpl implements AsyncDeploymentService {

	Logger log = LoggerFactory.getLogger(AsyncDeploymentServiceImpl.class);

	@Autowired
	private JobProgressService progressService;

	@Async
	@Override
	public void asyncCreateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
                                    Map<String, String> parameters, Plan plan, PlatformService platformService) {
		progressService.startJob(serviceInstance);

		try {
			deploymentService.syncCreateInstance(serviceInstance, parameters, plan, platformService);
		} catch (Exception e) {
			progressService.failJob(serviceInstance,
					"Internal error during Instance creation, please contact our support.");

			log.error("Exception during Instance creation", e);
			return;
		}
		progressService.succeedProgress(serviceInstance);
	}

	@Async
	@Override
	public void asyncDeleteInstance(DeploymentServiceImpl deploymentService, String instanceId,
			ServiceInstance serviceInstance, PlatformService platformService)
					throws ServiceInstanceDoesNotExistException {
		progressService.startJob(serviceInstance);

		try {
			deploymentService.syncDeleteInstance(serviceInstance, platformService);
		} catch (Exception e) {
			progressService.failJob(serviceInstance,
					"Internal error during Instance deletion, please contact our support.");

			log.error("Exception during Instance deletion", e);
			return;
		}
		progressService.succeedProgress(serviceInstance);
	}

	@Override
	public JobProgress getProgress(String serviceInstanceId) {
		try {
			return progressService.getProgress(serviceInstanceId);
		} catch (Exception e) {
			log.error("Error during job progress retrieval", e);
			return new JobProgress(JobProgress.UNKNOWN, "Error during job progress retrieval");
		}
	}

}
