package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The response from the broker sent back to the cloud controller on a
 * successful service instance creation request
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 * @author Christian Brinker, evoila.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceResponse {

	@JsonSerialize
	@JsonProperty("dashboard_url")
	private String dashboardUrl;
	
	private boolean isAsync;

	public ServiceInstanceResponse() {
	}

	public ServiceInstanceResponse(ServiceInstance serviceInstance, boolean isAsync) {
		this.dashboardUrl = serviceInstance.getDashboardUrl();
		this.isAsync = true;
	}

	public ServiceInstanceResponse(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public boolean isAsync() {
		return isAsync;
	}
}
