package de.evoila.cf.cpi.existing;

import de.evoila.cf.broker.bean.impl.ExistingEndpointBeanImpl;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Christian Brinker, evoila.
 *
 */

@Service
public abstract class ExistingServiceFactory implements PlatformService {
	
	private List<String> hosts = new ArrayList<String>();

	private int port;

	private String username;

	private String password;

	private String database;

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private PlatformRepository platformRepository;

	@Autowired
	private ServicePortAvailabilityVerifier portAvailabilityVerifier;
	
	@Autowired
	private ExistingEndpointBeanImpl existingServiceBean;
	
	@Override
	@PostConstruct
	public void registerCustomPlatformServie() {

		hosts = existingServiceBean.getHosts();
		port = existingServiceBean.getPort();
		username = existingServiceBean.getUsername();
		password = existingServiceBean.getPassword();
		database = existingServiceBean.getDatabase();
		
		platformRepository.addPlatform(Platform.EXISTING_SERVICE, this);
		log.info("Added Platform-Service " + this.getClass().toString() + " of type " + Platform.EXISTING_SERVICE 
				+ " with host: " + getHosts().stream().reduce((l,r) -> (l + ", " + r)).orElse("none") + " and port: " + getPort());
		
	}

	@Override
	public boolean isSyncPossibleOnCreate(Plan plan) {
		return false;
	}

	@Override
	public boolean isSyncPossibleOnDelete(ServiceInstance instance) {
		return false;
	}

	@Override
	public boolean isSyncPossibleOnUpdate(ServiceInstance instance, Plan plan) {
		return false;
	}

	@Override
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
		boolean available = false;
		try {
			available = portAvailabilityVerifier.verifyServiceAvailability(serviceInstance.getHosts(), false);
		} catch (Exception e) {
			throw new PlatformException("Service instance is not reachable. Service may not be started on instance.",
					e);
		}

		if (!available) {
			throw new PlatformException("Service instance is not reachable. Service may not be started on instance.");
		}

		return serviceInstance;
	}

	@Override
	public void preDeprovisionServiceInstance(ServiceInstance serviceInstance) {
	}

	@Override
	public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan) {
		return new ServiceInstance(instance, null, null);
	}

	@Override
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan) {
		return null;
	}

	@Override
	public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan,
			Map<String, String> customProperties) throws PlatformException {
		String instanceId = serviceInstance.getId();

		serviceInstance = new ServiceInstance(serviceInstance, "http://currently.not/available", instanceId,
				getExistingServiceHosts());

		provisionServiceInstance(serviceInstance, plan, customProperties);

		return serviceInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.cpi.existing.ExistingServiceFactory#getExistingServiceHosts(
	 * )
	 */
	protected List<ServerAddress> getExistingServiceHosts() {
		List<String> hosts = getHosts();
		List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
		for (String host: hosts) {
			ServerAddress serverAddress = new ServerAddress("existing_cluster", host, getPort());
			serverAddresses.add(serverAddress);
		}
		return serverAddresses;
	}

	public void deleteServiceInstance(ServiceInstance serviceInstance) throws PlatformException {
		try {
			CustomExistingServiceConnection connection = getCustomExistingService().connection(getHosts(), getPort(),
					getDatabase(), getUsername(), getPassword());

			String instanceId = serviceInstance.getId();
			deleteInstance(connection, instanceId);
		} catch (Exception e) {
			log.error(e.toString());
			throw new PlatformException("Could not delete service instance in existing instance server", e);
		}
	}

	protected abstract void deleteInstance(CustomExistingServiceConnection connection, String instanceId) throws PlatformException;

	protected abstract CustomExistingService getCustomExistingService();

	protected void provisionServiceInstance(ServiceInstance serviceInstance, Plan plan,
			Map<String, String> customProperties) throws PlatformException {
		try {
			CustomExistingServiceConnection connection = getCustomExistingService().connection(getHosts(), getPort(),
					getDatabase(), getUsername(), getPassword());

			String instanceId = serviceInstance.getId();
			createInstance(connection, instanceId);
			getCustomExistingService().bindRoleToInstanceWithPassword(connection, instanceId, instanceId, instanceId);
		} catch (Exception e) {
			log.error(e.toString());
			throw new PlatformException("Could not create service instance in existing instance server", e);
		}
	}

	protected abstract void createInstance(CustomExistingServiceConnection connection, String instanceId)  throws PlatformException;;

	public List<String> getHosts() {
		return hosts;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
