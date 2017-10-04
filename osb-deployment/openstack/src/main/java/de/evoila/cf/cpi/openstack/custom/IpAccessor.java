/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.List;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.ServerAddress;

/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract class IpAccessor {
	abstract public List<ServerAddress> getIpAddresses(String instanceId) throws PlatformException;
}
