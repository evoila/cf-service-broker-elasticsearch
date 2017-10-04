/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom.conf;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.cpi.openstack.custom.CustomIpAccessor;
import de.evoila.cf.cpi.openstack.custom.CustomStackHandler;
import de.evoila.cf.cpi.openstack.custom.DefaultIpAccessor;
import de.evoila.cf.cpi.openstack.custom.IpAccessor;
import de.evoila.cf.cpi.openstack.custom.StackHandler;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Configuration
@ConditionalOnBean(OpenstackBean.class)
public class OpenstackPlatformServiceConfig {

	private String networkId;

	@Autowired
	private OpenstackBean openstackBean;
	
	@PostConstruct
	private void initValues() {
		networkId = openstackBean.getNetworkId();
	}
	
	@ConditionalOnMissingBean(CustomIpAccessor.class)
	@Bean
	public IpAccessor ipAccessor() {
		return defaultIpAccessor();
	}

	@Bean
	public DefaultIpAccessor defaultIpAccessor() {
		return new DefaultIpAccessor(networkId);
	}

	@ConditionalOnMissingBean(CustomStackHandler.class)
	@Bean
	public StackHandler stackHandler() {
		return new StackHandler();
	}
}
