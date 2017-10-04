/**
 * 
 */
package de.evoila.cf.broker.model.fixture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.ServiceInstanceResponse;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public class ServiceInstanceFixture {

	public static List<ServiceInstance> getAllServiceInstances() {
		List<ServiceInstance> instances = new ArrayList<ServiceInstance>();
		instances.add(getServiceInstance());
		instances.add(getServiceInstanceTwo());
		return instances;
	}

	public static ServiceInstance getServiceInstance() {
		return new ServiceInstance("service-instance-one-id", "service-one-id", "plan-one-id",
				DataFixture.getOrgOneGuid(), DataFixture.getSpaceOneGuid(), new ConcurrentHashMap<String, String>(),
				"dashboard_url");
	}

	public static ServiceInstance getServiceInstanceTwo() {
		return new ServiceInstance("service-instance-two-id", "service-two-id", "plan-two-id",
				DataFixture.getOrgOneGuid(), DataFixture.getSpaceOneGuid(), new ConcurrentHashMap<String, String>(),
				"dashboard_url");
	}

	public static String getServiceInstanceId() {
		return "service-instance-id";
	}

	public static ServiceInstanceRequest getCreateServiceInstanceRequest() {
		ServiceDefinition service = ServiceFixture.getService();
		return new ServiceInstanceRequest(service.getId(), service.getPlans().get(0).getId(),
				DataFixture.getOrgOneGuid(), DataFixture.getSpaceOneGuid(), DataFixture.getContextOne());
	}

	public static String getCreateServiceInstanceRequestJson()
			throws JsonGenerationException, JsonMappingException, IOException {
		return DataFixture.toJson(getCreateServiceInstanceRequest());
	}

	public static ServiceInstanceResponse getCreateServiceInstanceResponse() {
		return new ServiceInstanceResponse("service-instance-url");
	}

}
