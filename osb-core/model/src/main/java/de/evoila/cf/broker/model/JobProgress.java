/**
 * 
 */
package de.evoila.cf.broker.model;

import java.util.Date;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public class JobProgress implements BaseEntity<String> {

	public static final String SUCCESS = "succeeded";

	public static final String FAILED = "failed";

	public static final String IN_PROGRESS = "in progress";
	
	public static final String UNKNOWN = "unknown";
	
	private String id;

	private String state;
	
	private Date date;
	
	private String description;
	
	public JobProgress() {
		super();
	}

	public JobProgress(String serviceInstanceId, String progress) {
		super();
		this.id = serviceInstanceId;
		this.state = progress;
		this.date = new Date();
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
