package de.evoila.cf.broker.bean;

import java.util.List;

public interface ExistingEndpointBean {

	public List<String> getHosts();
	public int getPort();
	public int getAdminport();
	public String getUsername();
	public String getPassword();
	public String getDatabase();
}
