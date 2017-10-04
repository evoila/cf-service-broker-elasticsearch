/**
 * 
 */
package de.evoila.cf.broker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.JobRepository;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Service
public class JobProgressService  {

	@Autowired
	private JobRepository jobRepository;

	public JobProgress getProgress(String serviceInstanceId) {
		return jobRepository.getJobProgress(serviceInstanceId);
	}

	public void startJob(ServiceInstance serviceInstance) {
		changeStatus(serviceInstance, JobProgress.IN_PROGRESS);
	}

	public void failJob(ServiceInstance serviceInstance, String description) {
		changeStatus(serviceInstance, JobProgress.FAILED);
	}

	public void succeedProgress(ServiceInstance serviceInstance) {
		changeStatus(serviceInstance, JobProgress.SUCCESS);
	}

	private void changeStatus(ServiceInstance serviceInstance, String newStatus) {
		jobRepository.saveOrUpdateJobProgress(serviceInstance.getId(), newStatus);
	}
}
