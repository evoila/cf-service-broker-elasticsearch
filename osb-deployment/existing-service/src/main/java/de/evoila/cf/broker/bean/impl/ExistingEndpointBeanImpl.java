package de.evoila.cf.broker.bean.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.bean.ExistingEndpointBean;

@Service
@ConfigurationProperties(prefix="existing.endpoint")
public class ExistingEndpointBeanImpl implements ExistingEndpointBean {

	private List<String> hosts = new ArrayList<String>();

	private int port;
	
	private int adminport;

	private String username;

	private String password;

	private String database;

	public List<String> getHosts() {
		return hosts;
	}

	public int getPort() {
		return port;
	}
	
	public int getAdminport() {
		return adminport;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDatabase() {
		return database;
	}
	
	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void setAdminport(int adminport) {
		this.adminport = adminport;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
