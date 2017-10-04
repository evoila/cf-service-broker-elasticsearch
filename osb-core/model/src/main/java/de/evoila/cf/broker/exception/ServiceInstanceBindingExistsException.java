package de.evoila.cf.broker.exception;

/**
 * Thrown when a duplicate request to bind to a service instance is received.
 * 
 * @author sgreenberg@gopivotal.com
 */
public class ServiceInstanceBindingExistsException extends Exception {

	private static final long serialVersionUID = -914571358227517785L;

	private String bindingId;

	private String serviceInstanceId;

	public ServiceInstanceBindingExistsException(String bindingId, String serviceInstanceId) {
		this.bindingId = bindingId;
		this.serviceInstanceId = serviceInstanceId;
	}

	@Override
	public String getMessage() {
		return "ServiceInstanceBinding already exists: serviceInstanceBinding.id = " + bindingId
				+ ", serviceInstance.id = " + serviceInstanceId;
	}
}
