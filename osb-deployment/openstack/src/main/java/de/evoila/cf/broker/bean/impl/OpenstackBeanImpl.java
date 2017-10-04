package de.evoila.cf.broker.bean.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.bean.OpenstackBean;

@Service
@ConfigurationProperties(prefix="openstack")
@ConditionalOnProperty(prefix="openstack",
		name = {"endpoint",
				"user.username", "user.password", "user.domainName",
				"project.domainName", "project.projectName",
				"networkId", "subnetId", "imageId", "keypair",
				"cinder.az"
		}, havingValue="")
public class OpenstackBeanImpl implements OpenstackBean {

	private String endpoint;
	
	private User user;
	
	private Project project;
	
	private String networkId;
	
	private String subnetId;
	
	private String imageId;
	
	private String keypair;
	
	private Cinder cinder;
	
	public static class User {
		private String username;
		
		private String password;
		
		private String domainName;

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public String getDomainName() {
			return domainName;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void setDomainName(String domainName) {
			this.domainName = domainName;
		}
	}
	
	public static class Project {
		private String domainName;
		
		private String projectName;

		public String getDomainName() {
			return domainName;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setDomainName(String domainName) {
			this.domainName = domainName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}		
	}
	
	public static class Cinder {
		private String az;

		public String getAz() {
			return az;
		}

		public void setAz(String az) {
			this.az = az;
		}
	}

	public String getEndpoint() {
		return endpoint;
	}

	public User getUser() {
		return user;
	}

	public Project getProject() {
		return project;
	}

	public String getNetworkId() {
		return networkId;
	}

	public String getSubnetId() {
		return subnetId;
	}

	public String getImageId() {
		return imageId;
	}

	public String getKeypair() {
		return keypair;
	}

	public Cinder getCinder() {
		return cinder;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public void setKeypair(String keypair) {
		this.keypair = keypair;
	}

	public void setCinder(Cinder cinder) {
		this.cinder = cinder;
	}
}
