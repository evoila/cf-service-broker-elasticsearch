/**
 * 
 */
package de.evoila.cf.cpi.existing;

import java.util.List;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface CustomExistingService {

	CustomExistingServiceConnection connection(List<String> hosts, int port, String database, String username, String password) throws Exception;

	void bindRoleToInstanceWithPassword(CustomExistingServiceConnection connection, String database,
			String username, String password) throws Exception;

}
