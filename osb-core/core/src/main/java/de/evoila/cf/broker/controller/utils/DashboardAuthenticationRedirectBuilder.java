/**
 * 
 */
package de.evoila.cf.broker.controller.utils;

import de.evoila.cf.broker.model.Dashboard;
import de.evoila.cf.broker.model.DashboardClient;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Johannes Hiemer.
 *
 */
public class DashboardAuthenticationRedirectBuilder {
	
	private String baseUrl;

	private String clientId;
	
	private String redirectUri;
	
	private String scopes;
	
	public DashboardAuthenticationRedirectBuilder(Dashboard dashboard, DashboardClient dashboadClient, String redirectUri,
			String scopes) {
		super();
		this.baseUrl = dashboard.getAuthEndpoint();
		this.clientId = dashboadClient.getId();
		this.redirectUri = redirectUri;
		this.scopes = scopes;
	}
	
	public String getRedirectUrl() throws URISyntaxException {
		URIBuilder builder = new URIBuilder(this.baseUrl + "/authorize")				
				.addParameter("client_id", this.clientId)
				.addParameter("redirect_uri", new URI(this.redirectUri).toString())
				.addParameter("response_type", "code")
				.addParameter("scopes", scopes);

		URI uri = new URI(builder.toString());

		return uri.toString();
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getScopes() {
		return scopes;
	}

	public void setScopes(String scopes) {
		this.scopes = scopes;
	}
	
}
