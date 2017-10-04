/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import java.util.List;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Keypair;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;


/**
 * @author Johannes Hiemer.
 *
 */
@Component
@ConditionalOnBean(OpenstackBean.class)
public class NovaFluent {
	
	private OSClient client() {
		return OpenstackConnectionFactory.connection();
	}
	
	public List<? extends Keypair> keyPairs() {
		return client().compute().keypairs().list();
	}
	
	public void createKeyPair(String name, String publicKey) {
		client().compute().keypairs().create(name, publicKey);
	}
	
	public List<? extends Flavor> getFlavors() {
		return client().compute().flavors().list();
	}
	
	public Server create(String name, String keypairName, String imageId, String flavorId, 
			List<String> networks, boolean powerOn) {
		Assert.notNull(client());
		
		ServerCreate serverCreate = client().compute().servers().serverBuilder()
			.name(name)
			.keypairName(keypairName)
			.image(imageId)
			.flavor(flavorId)
			.networks(networks)
			.build();
		
		Server server = null;
		if (powerOn)
			server = client().compute().servers().boot(serverCreate) ;
		
		return server;
	}
	
	public void deleteInstance(String serverId) {
		client().compute().servers().delete(serverId);
	}
	
	public void attach(String serverId, String volumeId, String device) {
		client().compute().servers().attachVolume(serverId, volumeId, device);
	}
	
	public String ip(Server server, String subnet) {
		return server.getAddresses().getAddresses().get(subnet).get(0).getAddr();
	}
	
}
