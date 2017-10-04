/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import java.util.List;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;


/**
 * @author Johannes Hiemer.
 *
 */
@Component
@ConditionalOnBean(OpenstackBean.class)
public class NeutronFluent {
	
	private OSClient client() {
		return OpenstackConnectionFactory.connection();
	}
	
	public List<? extends Network> networks() {
		return client().networking().network().list();
	}
	
	public Network get(String id) {
		return client().networking().network().get(id);
	}
	
	public Subnet subnet(String id, String subnetId) {
		Network network = client().networking().network().get(id);
		if (network != null) {
			List<? extends Subnet> subnets = network.getNeutronSubnets();
			for (Subnet subnet : subnets)
				if (subnet.getId().equals(subnetId))
					return subnet;
		}
		return null;
	}
	
}
