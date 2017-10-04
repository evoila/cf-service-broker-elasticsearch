/**
 * 
 */
package de.evoila.cf.broker.model.data;

import java.util.UUID;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.VolumeUnit;

/**
 * @author Johannes Hiemer.
 *
 */
public class ServiceInstanceData {
	
	public static Plan createDockerPlanData() {
		Plan dockerPlan = new Plan("docker", "50 MB PostgreSQL DB Basic Instance",
				"The most basic PostgreSQL plan currently available. Providing"
			+ "50 MB of capcity in a PostgreSQL DB.", Platform.DOCKER, 25, VolumeUnit.M, "3", true, 100);
		return dockerPlan;
	}
	
	public static ServiceInstance createDockerData() {
		
		ServiceInstance serviceInstance = new ServiceInstance(UUID.randomUUID().toString(), 
				UUID.randomUUID().toString(), createDockerData().getId(), 
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), 
				null, "http://currently.not/available");
		
		return serviceInstance;
	}
	
	public static Plan createOpenstackPlanData() {
		Plan openstackPlan = new Plan("openstack", "500 MB PostgreSQL DB Basic Instance",
				"The most basic PostgreSQL plan currently available. Providing"
			+ "500 MB of capcity in a PostgreSQL DB.", Platform.OPENSTACK, 500, VolumeUnit.M, "3", true, 1000);
		return openstackPlan;
	}
	
	public static ServiceInstance createOpenstackData() {
		
		ServiceInstance serviceInstance = new ServiceInstance(UUID.randomUUID().toString(), 
				UUID.randomUUID().toString(), createOpenstackPlanData().getId(), 
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), 
				null, "http://currently.not/available");
		
		return serviceInstance;
	}

}
