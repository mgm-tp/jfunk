package com.mgmtp.jfunk.web;

/**
 * @author rnaegele
 * @version $Id$
 */
public class HtmlUnitSSLParams {
	private final String keyStore;
	private final String keyStorePassword;
	private final String keyStoreType;
	private final String trustStore;
	private final String trustStorePassword;
	private final String trustStoreType;

	public HtmlUnitSSLParams(final String keyStore, final String keyStorePassword, final String keyStoreType, final String trustStore,
			final String trustStorePassword, final String trustStoreType) {
		this.keyStore = keyStore;
		this.keyStorePassword = keyStorePassword;
		this.keyStoreType = keyStoreType;
		this.trustStore = trustStore;
		this.trustStorePassword = trustStorePassword;
		this.trustStoreType = trustStoreType;
	}

	/**
	 * @return the keyStore
	 */
	public String getKeyStore() {
		return keyStore;
	}

	/**
	 * @return the keyStorePassword
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	/**
	 * @return the keyStoreType
	 */
	public String getKeyStoreType() {
		return keyStoreType;
	}

	/**
	 * @return the trustStore
	 */
	public String getTrustStore() {
		return trustStore;
	}

	/**
	 * @return the trustStorePassword
	 */
	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	/**
	 * @return the trustStoreType
	 */
	public String getTrustStoreType() {
		return trustStoreType;
	}
}
