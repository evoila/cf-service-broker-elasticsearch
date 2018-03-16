/**
 * 
 */
package de.evoila.cf.cpi.custom.props;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 *
 */
public class ElasticsearchCustomPropertyHandler implements DomainBasedCustomPropertyHandler {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * addDomainBasedCustomProperties(de.evoila.cf.broker.model.Plan, java.util.Map, java.lang.String)
	 */
	@Override
	public Map<String, String> addDomainBasedCustomProperties(Plan plan, Map<String, String> customProperties,
                                                              ServiceInstance serviceInstance) {
		
		return customProperties;
	}

}
