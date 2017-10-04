package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.service.PlatformService;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface PlatformRepository {

	// Depl
	public void addPlatform(Platform platform, PlatformService platformService);

	// Depl
	public PlatformService getPlatformService(Platform platform);

}