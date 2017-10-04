/**
 * 
 */
package de.evoila.cf.broker.model;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class ServerAddress {
	
	private String ip;
	private int port;
	private String name;

	public ServerAddress() {
		super();
	}

	public ServerAddress(String name) {
		super();
		this.name = name;
	}

	public ServerAddress(String name, String ip) {
		super();
		this.name = name;
		this.ip = ip;
	}

	public ServerAddress(String name, String ip, int port) {
		super();
		this.name = name;
		this.ip = ip;
		this.port = port;
	}

	public ServerAddress(ServerAddress address) {
		super();
		this.name = address.name;
		this.ip = address.ip;
		this.port = address.port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
