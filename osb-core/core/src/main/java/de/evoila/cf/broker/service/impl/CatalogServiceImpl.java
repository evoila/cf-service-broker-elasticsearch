package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.service.CatalogService;

/**
 * An implementation of the CatalogService that gets the catalog injected (ie
 * configure in spring config)
 * 
 * @author sgreenberg@gopivotal.com
 * @author Christian Brinker, evoila.
 */
@Service
public class CatalogServiceImpl implements CatalogService {

	private Catalog catalog;

	private Map<String, ServiceDefinition> serviceDefs = new ConcurrentHashMap<String, ServiceDefinition>();

	@Autowired
	public CatalogServiceImpl(Catalog catalog) {
		this.catalog = catalog;
		initializeMap();
	}

	private void initializeMap() {
		for (ServiceDefinition def : catalog.getServices()) {
			serviceDefs.put(def.getId(), def);
		}
	}

	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return serviceDefs.get(serviceId);
	}

}
