/**
 * 
 */
package de.evoila.cf.broker.persistence.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.repository.RouteBindingRepository;

/**
 * @author Patrick Weber, evoila.
 *
 */
@Service
public class RouteBindingRepositoryImpl implements RouteBindingRepository {


	@Autowired
	de.evoila.cf.broker.persistence.mongodb.repository.RouteBindingRepository routeBindingRepository;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.repository.BindingRepository#getInternalBindingId(
	 * java.lang.String)
	 */
	@Override
	public String getRouteBindingId(String bindingId) {
		return routeBindingRepository.findOne(bindingId).getServiceInstanceId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.repository.BindingRepository#addInternalBinding(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public void addRouteBinding(RouteBinding binding) {
		routeBindingRepository.save(binding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.repository.BindingRepository#
	 * containsInternalBindingId(java.lang.String)
	 */
	@Override
	public boolean containsRouteBindingId(String bindingId) {
		return routeBindingRepository.exists(bindingId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.repository.BindingRepository#deleteBinding(java.lang.
	 * String)
	 */
	@Override
	public void deleteRouteBinding(String bindingId) {
		routeBindingRepository.delete(bindingId);
	}

	@Override
	public RouteBinding findOne(String bindingId) {
		return routeBindingRepository.findOne(bindingId);
	}
}
