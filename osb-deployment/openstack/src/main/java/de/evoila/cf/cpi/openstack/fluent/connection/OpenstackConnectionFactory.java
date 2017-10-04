/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent.connection;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author Johannes Hiemer, evoila.
 *
 */
public class OpenstackConnectionFactory {

	private final static Logger log = LoggerFactory.getLogger(OpenstackConnectionFactory.class);

	protected static OSClient osClient;
	
	private static String username;
	private static String password;
	private static String authUrl;
	private static String projectDomainName;
	private static String projectName;

	private static String PROVIDER = "openstack-fluent--provider";

	private static OpenstackConnectionFactory instance = null;

	private static String userDomainName;

	public static OpenstackConnectionFactory getInstance() {
		if (instance == null) {
			instance = new OpenstackConnectionFactory();
		}
		return instance;
	}

	public OpenstackConnectionFactory setCredential(String username, String password, String userDomainName) {
		OpenstackConnectionFactory.username = username;
		OpenstackConnectionFactory.password = password;
		OpenstackConnectionFactory.userDomainName = userDomainName;
		return instance;
	}

	public OpenstackConnectionFactory authenticate(String authUrl, String projectName, String projectDomain) {
		OpenstackConnectionFactory.authUrl = authUrl;
		OpenstackConnectionFactory.projectDomainName = projectDomain;
		OpenstackConnectionFactory.projectName = projectName;
		Assert.notNull(username, "Username may not be empty, when initializing");
		Assert.notNull(password, "Password may not be empty, when initializing");
		Assert.notNull(projectDomain, "Project Domain Name may not be empty, when initializing");

		authenticate();
		return instance;
	}

	private static void authenticate() {
		osClient = OSFactory.builderV3().endpoint(authUrl).credentials(username, password, Identifier.byName(userDomainName))
				.scopeToProject(Identifier.byName(projectName), Identifier.byName(projectDomainName))
				.authenticate();
	}

	public static OpenstackConnectionFactory authenticateV3(String authUrl, String userDomainName) {
		Assert.notNull(username, "Username may not be empty, when initializing");
		Assert.notNull(password, "Password may not be empty, when initializing");

		log.info("Initializing Provider:" + PROVIDER);

		osClient = OSFactory.builderV3().endpoint(authUrl).credentials(username, password, Identifier.byName(userDomainName))
				.authenticate();
		return instance;
	}

	public static OSClient connection() {
		authenticate();
		Assert.notNull(osClient, "Connection must be initialized before called any methods on it");
		Assert.notNull(osClient.getToken(), "No token defined, connection details are invalid");
		return osClient;
	}

}
