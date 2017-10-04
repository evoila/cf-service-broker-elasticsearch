package de.evoila.cf.broker.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A binding to a service instance
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
public class ServiceInstanceBinding implements BaseEntity<String> {

	private String id;

	private String serviceInstanceId;

	private Map<String, Object> credentials = new HashMap<String, Object>();

	private String syslogDrainUrl;

	private String appGuid;

	private Map<String, String> parameters;

	private List<ServerAddress> externalServerAddresses;

	public ServiceInstanceBinding() {
		super();
	}

	public ServiceInstanceBinding(String id, String serviceInstanceId, Map<String, Object> credentials,
			String syslogDrainUrl) {
		this.id = id;
		this.serviceInstanceId = serviceInstanceId;
		setCredentials(credentials);
		this.syslogDrainUrl = syslogDrainUrl;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public Map<String, Object> getCredentials() {
		return credentials;
	}

	private void setCredentials(Map<String, Object> credentials) {
		if (credentials == null) {
			credentials = new HashMap<String, Object>();
		} else {
			this.credentials = credentials;
		}
	}

	public String getSyslogDrainUrl() {
		return syslogDrainUrl;
	}

	public String getAppGuid() {
		return appGuid;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public List<ServerAddress> getExternalServerAddresses() {
		return externalServerAddresses;
	}

	public void setExternalServerAddresses(List<ServerAddress> externalServerAddresses) {
		this.externalServerAddresses = externalServerAddresses;
	}

}
