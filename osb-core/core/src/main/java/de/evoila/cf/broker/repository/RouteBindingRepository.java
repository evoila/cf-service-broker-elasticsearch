package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.RouteBinding;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface RouteBindingRepository {

	// Bind
	String getRouteBindingId(String bindingId);

	// Bind
	void addRouteBinding(RouteBinding binding);

	// Bind
	boolean containsRouteBindingId(String bindingId);

	// Bind
	void deleteRouteBinding(String bindingId);

	RouteBinding findOne(String bindingId);

}