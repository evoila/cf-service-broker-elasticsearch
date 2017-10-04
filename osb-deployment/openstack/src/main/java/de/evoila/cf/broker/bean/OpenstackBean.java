package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.bean.impl.OpenstackBeanImpl.Cinder;
import de.evoila.cf.broker.bean.impl.OpenstackBeanImpl.Project;
import de.evoila.cf.broker.bean.impl.OpenstackBeanImpl.User;

public interface OpenstackBean {

	public String getEndpoint();
	public User getUser();
	public Project getProject();
	public String getNetworkId();
	public String getSubnetId();
	public String getImageId();
	public String getKeypair();
	public Cinder getCinder();
	
}