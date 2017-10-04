package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The response from the broker sent back to the cloud controller on a
 * successful service instance creation request
 * 
 * @author Johannes Hiemer.
 * 
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class JobProgressResponse {

	/**
	 * Allowed values are: "in progress" "succeeded" "failed"
	 */
	@JsonSerialize
	@JsonProperty("state")
	private String state;

	@JsonSerialize
	@JsonProperty("description")
	private String description;

	public JobProgressResponse() {
		super();
	}
	
	public JobProgressResponse(JobProgress jobProgress) {
		super();
		this.state = jobProgress.getState();
		this.description = jobProgress.getDescription();
	}

	public JobProgressResponse(String state, String description) {
		super();
		this.state = state;
		this.description = description;
	}

	public String getState() {
		return state;
	}

	public String getDescription() {
		return description;
	}
}
