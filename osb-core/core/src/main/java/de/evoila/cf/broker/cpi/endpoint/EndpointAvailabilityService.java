/**
 * 
 */
package de.evoila.cf.broker.cpi.endpoint;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.cpi.AvailabilityState;
import de.evoila.cf.broker.model.cpi.EndpointServiceState;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
@Scope(value  = "singleton")
public class EndpointAvailabilityService {

	private Map<String, EndpointServiceState> services = new HashMap<String, EndpointServiceState>();

	public Map<String, EndpointServiceState> getServices() {
		return services;
	}

	public void setServices(Map<String, EndpointServiceState> services) {
		this.services = services;
	}
	
	public void add(String key, EndpointServiceState endpointServiceState) {
		this.services.put(key, endpointServiceState);
	}
	
	public boolean isAvailable(String key) {
		EndpointServiceState endpointServiceState = this.services.get(key);
		if (endpointServiceState == null)
			return true;
		else
			return endpointServiceState.getState().equals(AvailabilityState.AVAILABLE);
	}
}
