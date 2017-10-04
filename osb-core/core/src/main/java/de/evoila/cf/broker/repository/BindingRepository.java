package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.ServiceInstanceBinding;

import java.util.List;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface BindingRepository {

	// Bind
	String getInternalBindingId(String bindingId);

	// Bind
	void addInternalBinding(ServiceInstanceBinding binding);

	// Bind
	boolean containsInternalBindingId(String bindingId);

	// Bind
	void deleteBinding(String bindingId);

	ServiceInstanceBinding findOne(String bindingId);

    List<ServiceInstanceBinding> getBindingsForServiceInstance (String serviceInstanceId);
}