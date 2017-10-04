/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.heat.StackService;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.heat.Resource;
import org.openstack4j.model.heat.Stack;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;

/**
 * @author Johannes Hiemer.
 *
 */
@Component
@ConditionalOnBean(OpenstackBean.class)
public class HeatFluent {

	public static String NOVA_INSTANCE_TYPE = "OS::Nova::Server";

	public static String CINDER_INSTANCE_TYPE = "OS::Cinder::Volume";

	private OSClient client() {
		return OpenstackConnectionFactory.connection();
	}

	public StackService list() {
		return client().heat().stacks();
	}

	public Stack create(String name, String template, Map<String, String> parameters, boolean disableRollback,
			Long timeOutMins) {
		Stack stack = client()
				.heat()
				.stacks()
				.create(Builders.stack()
						.name(name)
						.template(template)
						.parameters(parameters)
						.disableRollback(disableRollback)
						.timeoutMins(timeOutMins)
						.build());

		return stack;
	}
	
	public Stack create(String name, String template, Map<String, String> parameters, boolean disableRollback,
			Long timeOutMins, Map<String, String> files) {
		Stack stack = client()
				.heat()
				.stacks()
				.create(Builders.stack()
						.name(name)
						.template(template)
						.files(files)
						.parameters(parameters)
						.disableRollback(disableRollback)
						.timeoutMins(timeOutMins)
						.build());

		return stack;
	}

	public void delete(String stackName, String stackId) {
		client().heat().stacks().delete(stackName, stackId);
	}

	public Stack get(String name) {
		return client().heat().stacks().getStackByName(name);
	}

	public List<? extends Resource> resources(String name, String stackId) {
		return client().heat().resources().list(name, stackId);
	}

	public List<? extends Resource> filter(String name, String stackId, String type) {
		List<Resource> filteredResources = new ArrayList<Resource>();
		List<? extends Resource> resources = resources(name, stackId);

		for (Resource resource : resources)
			if (resource.getType().equals(type))
				filteredResources.add(resource);

		return filteredResources;
	}

	public List<Server> servers(String name, String stackId, String type) throws PlatformException {
		List<Server> servers = new ArrayList<Server>();
		List<? extends Resource> resources = filter(name, stackId, type);

		for (Resource resource : resources) {
			Server server = client().compute().servers().get(resource.getPhysicalResourceId());
			if (server == null) {
				throw new PlatformException("Server " + name + " does not exist");
			}
			servers.add(server);
		}

		return servers;
	}
	
	public static String uniqueName(String instanceId) {
		return "s" + instanceId;
	}
}
