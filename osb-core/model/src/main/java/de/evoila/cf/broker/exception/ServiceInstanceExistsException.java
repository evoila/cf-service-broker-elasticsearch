package de.evoila.cf.broker.exception;

/**
 * Thrown when a duplicate service instance creation request is received.
 * 
 * @author sgreenberg@gopivotal.com
 */
public class ServiceInstanceExistsException extends Exception {

	private static final long serialVersionUID = -914571358227517785L;

	private String instanceId;

	private String serviceId;

	public ServiceInstanceExistsException(String instanceId, String serviceId) {
		this.instanceId = instanceId;
		this.serviceId = serviceId;
	}

	@Override
	public String getMessage() {
		return "ServiceInstance with the given id already exists: ServiceInstance.id = " + instanceId
				+ ", Service.id = " + serviceId;
	}
}
