/**
 * 
 */
package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Johannes Hiemer
 */
public class Dashboard {

	@JsonSerialize
	@JsonProperty("url")
	private String url;
	
	@JsonSerialize
	@JsonProperty("auth_endpoint")
	private String authEndpoint;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthEndpoint() {
		return authEndpoint;
	}

	public void setAuthEndpoint(String authEndpoint) {
		this.authEndpoint = authEndpoint;
	}
	
}
