/**
 * 
 */
package de.evoila.cf.broker.persistence.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.stereotype.Repository;

import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.PlatformService;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Repository
public class PlatformRepositoryImpl implements PlatformRepository {

	private Map<Platform, PlatformService> platformServices = new ConcurrentHashMap<Platform, PlatformService>();

	// public Map<Platform, PlatformService> getPlatformServices() {
	// return platformServices;
	// }

	// Depl
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.PlatformRepositroy#addPlatform(de.
	 * evoila.cf.broker.model.Platform,
	 * de.evoila.cf.broker.service.PlatformService)
	 */
	@Override
	public void addPlatform(Platform platform, PlatformService platformService) {
		if (platformServices.get(platform) == null)
			platformServices.put(platform, platformService);
		else
			throw new BeanCreationException("Cannot add multiple instances of platform service to PlatformRepository");
	}

	// Depl
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.PlatformRepositroy#getPlatform(de.
	 * evoila.cf.broker.model.Platform)
	 */
	@Override
	public PlatformService getPlatformService(Platform platform) {
		return platformServices.get(platform);
	}

}
