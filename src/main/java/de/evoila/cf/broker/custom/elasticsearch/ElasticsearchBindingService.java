/**
 * 
 */
package de.evoila.cf.broker.custom.elasticsearch;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class ElasticsearchBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	public void create(ServiceInstance serviceInstance, Plan plan) {
		log.debug("created Binding");
	}

	public void delete(ServiceInstance serviceInstance, Plan plan) {
		log.debug("deleted Binding");
	}

	@Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
			ServerAddress host) throws ServiceBrokerException {

		String dbURL = String.format("http://%s:%d", host.getIp(), host.getPort());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return credentials;
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		log.debug("unbind Service");
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#bindRoute(de.evoila.
	 * cf.broker.model.ServiceInstance, java.lang.String)
	 */
	@Override
	protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
		throw new UnsupportedOperationException();
	}

}
