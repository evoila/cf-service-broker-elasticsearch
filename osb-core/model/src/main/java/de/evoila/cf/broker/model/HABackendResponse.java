/**
 * 
 */
package de.evoila.cf.broker.model;

/**
 * @author Sebastian Boeing, evoila.
 *
 */
public class HABackendResponse {
	private String ip;

	private int port;

	private String identifier;

	/**
	 * @param ip
	 * @param port
	 * @param identifier
	 */
	public HABackendResponse(String ip, int port, String identifier) {
		this.ip = ip;
		this.port = port;
		this.identifier = identifier;		
	}
	
	public HABackendResponse() {
		
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

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String name) {
		this.identifier = name;
	}
}
