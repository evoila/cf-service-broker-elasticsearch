/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.storage.block.Volume;

import java.util.List;

/**
 * @author Johannes Hiemer.
 *
 */
public class CinderFluent {
	
	private OSClient client() {
		return OpenstackConnectionFactory.connection();
	}
	
	public List<? extends Volume> list() {
		return client().blockStorage().volumes().list();
	}
	
	public Volume get(String volumeId) {
		return client().blockStorage().volumes().get(volumeId);
	}
	
	public ActionResponse delete(String volumeId) {
		return client().blockStorage().volumes().delete(volumeId);
	}
	
	public Volume create(String name, String description, int size) {
		return client().blockStorage().volumes().create(Builders.volume()
				.name(name)
				.description(description)
				.size(size).build()
				);
	}
	
	public Volume create(String name, String description, int size, 
			String imageRef, boolean isBootable) {
		return client().blockStorage().volumes().create(Builders.volume()
				.name(name)
				.description(description)
				.size(size)
				.imageRef(imageRef)
				.bootable(isBootable)
				.build()
				);
	}
}
