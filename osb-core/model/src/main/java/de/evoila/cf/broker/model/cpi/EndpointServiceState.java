/**
 * 
 */
package de.evoila.cf.broker.model.cpi;

import java.util.Date;

/**
 * @author Johannes Hiemer.
 *
 */
public class EndpointServiceState {
	
	private String identifier;
	
	private Date date;
	
	private AvailabilityState state;
	
	private String information;
	
	public EndpointServiceState(String identifier, AvailabilityState state, String information) {
		super();
		this.identifier = identifier;
		this.date = new Date();
		this.state = state;
		this.information = information;
	}
	
	public EndpointServiceState(String identifier, AvailabilityState state) {
		super();
		this.identifier = identifier;
		this.date = new Date();
		this.state = state;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public AvailabilityState getState() {
		return state;
	}

	public void setState(AvailabilityState state) {
		this.state = state;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}
}
