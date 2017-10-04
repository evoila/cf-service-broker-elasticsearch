/**
 * 
 */
package de.evoila.cf.broker.model.data;

import java.util.UUID;

import de.evoila.cf.broker.model.ServiceInstanceRequest;

/**
 * @author Johannes Hiemer.
 *
 */
public class ServiceInstanceRequestData {
	
	public static ServiceInstanceRequest createServiceInstanceRequest(String serviceDefinitionId, String planId) {
		ServiceInstanceRequest serviceInstanceRequest = new ServiceInstanceRequest();
		serviceInstanceRequest.setOrganizationGuid(UUID.randomUUID().toString());
		serviceInstanceRequest.setSpaceGuid(UUID.randomUUID().toString());
		serviceInstanceRequest.setServiceDefinitionId(serviceDefinitionId);
		serviceInstanceRequest.setPlanId(planId);
		
		return serviceInstanceRequest;
	}

}
