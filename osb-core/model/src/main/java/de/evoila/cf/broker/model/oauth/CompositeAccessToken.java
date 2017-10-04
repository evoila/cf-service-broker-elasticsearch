/**
 * 
 */
package de.evoila.cf.broker.model.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Johannes Hiemer.
 *
 */
public class CompositeAccessToken {

	/**
	 * {
		  "access_token" : "77cc685e8e244bd88ef3e7227ab15110",
		  "token_type" : "bearer",
		  "refresh_token" : "655b19516a6f4c4a949244e88569a514-r",
		  "expires_in" : 43199,
		  "scope" : "openid oauth.approvals",
		  "jti" : "77cc685e8e244bd88ef3e7227ab15110"
		}
	 *	
	 */
	@JsonProperty("expires_in")
	private int expiresIn;

	private String tokenType = "bearer";

	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("refresh_token")
	private String refreshToken;

	@JsonProperty("scope")
	private String scope;

	@JsonProperty("jti")
	private String jti;

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

}
