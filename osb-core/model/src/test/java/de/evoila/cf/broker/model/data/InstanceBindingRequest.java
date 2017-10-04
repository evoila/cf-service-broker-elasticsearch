/**
 * 
 */
package de.evoila.cf.broker.model.data;

import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class InstanceBindingRequest {
	public static ServiceInstanceBindingRequest createBindingRequest(String appGuid, String planId,
			String serviceDefinitionId) {
		ServiceInstanceBindingRequest bindingRequest = new ServiceInstanceBindingRequest(serviceDefinitionId, planId,
				appGuid, null);
		return bindingRequest;
	}
}
