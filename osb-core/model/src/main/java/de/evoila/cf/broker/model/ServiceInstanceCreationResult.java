/**
 * 
 */
package de.evoila.cf.broker.model;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class ServiceInstanceCreationResult {

	/**
	 * 
	 */
	private String internalId;

	/**
	 * 
	 */
	private String daschboardUrl;

	/**
	 * 
	 */
	public ServiceInstanceCreationResult() {
		super();
	}

	/**
	 * @param internalId
	 * @param daschboardUrl
	 */
	public ServiceInstanceCreationResult(String internalId, String daschboardUrl) {
		super();
		this.internalId = internalId;
		this.daschboardUrl = daschboardUrl;
	}

	/**
	 * @return the internalId
	 */
	public String getInternalId() {
		return internalId;
	}

	/**
	 * @param internalId
	 *            the internalId to set
	 */
	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	/**
	 * @return the daschboardUrl
	 */
	public String getDaschboardUrl() {
		return daschboardUrl;
	}

	/**
	 * @param daschboardUrl
	 *            the daschboardUrl to set
	 */
	public void setDaschboardUrl(String daschboardUrl) {
		this.daschboardUrl = daschboardUrl;
	}

}
