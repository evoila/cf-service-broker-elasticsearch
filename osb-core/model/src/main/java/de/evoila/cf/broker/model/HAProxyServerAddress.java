/**
 * 
 */
package de.evoila.cf.broker.model;

import java.util.List;

/**
 * @author Rene Schollmeyer, Sebastian Boeing, evoila
 *
 */
public class HAProxyServerAddress extends ServerAddress {

	private String mode;
	
	private List<String> options;

	public HAProxyServerAddress() {
		super();
	}
	
	public HAProxyServerAddress(ServerAddress serverAddress, Mode mode, List<String> options) {
		super(serverAddress);
		this.mode = mode.toString();
		this.options= options;
	}
	
	public HAProxyServerAddress(HAProxyServerAddress haProxyServerAddress, String name) {
		this.setIp(haProxyServerAddress.getIp());
		this.setPort(haProxyServerAddress.getPort());
		this.setName(name); 
		this.setMode(haProxyServerAddress.getMode());
		this.setOptions(haProxyServerAddress.getOptions());
	}
	
	public HAProxyServerAddress(String name) {
		super(name);
	}
	
	public HAProxyServerAddress(String name, String ip) {
		super(name, ip);
	}
	
	public HAProxyServerAddress(String name, String ip, int port) {
		super(name, ip, port);
	}
	
	public HAProxyServerAddress(String name, String ip, int port, String mode) {
		super(name, ip, port);
		this.mode = mode;
	}
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
}
