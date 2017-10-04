package de.evoila.cf.broker.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The catalog of services offered by this broker.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */

@ConfigurationProperties("catalog")
public class Catalog {

	private List<ServiceDefinition> services = new ArrayList<ServiceDefinition>();
	
	public Catalog() {
		
	}
	
	public Catalog(List<ServiceDefinition> services) {
		this.setServices(services); 
	}
	
	public List<ServiceDefinition> getServices() {
		return services;
	}

	private void setServices(List<ServiceDefinition> services) {
		if ( services == null ) {
			this.services = new ArrayList<ServiceDefinition>();
		} else {
			this.services= services;
		} 
	}
}
