package com.mgmtp.jfunk.web;

/**
 * @author rnaegele
 * @version $Id$
 */
public class HtmlUnitWebDriverParams {

	private final int connectionTimeout;
	private final boolean refuseCookies;
	private final boolean redirect;
	private final boolean javascriptEnabled;
	private final boolean validateJavascript;
	private final boolean ignoreResponseCode;
	private final boolean autoRefresh;
	private final boolean logIncorrectCode;

	public HtmlUnitWebDriverParams(final int connectionTimeout, final boolean refuseCookies, final boolean redirect, final boolean javascriptEnabled,
			final boolean validateJavascript, final boolean ignoreResponseCode, final boolean autoRefresh, final boolean logIncorrectCode) {
		this.connectionTimeout = connectionTimeout;
		this.refuseCookies = refuseCookies;
		this.redirect = redirect;
		this.javascriptEnabled = javascriptEnabled;
		this.validateJavascript = validateJavascript;
		this.ignoreResponseCode = ignoreResponseCode;
		this.autoRefresh = autoRefresh;
		this.logIncorrectCode = logIncorrectCode;
	}

	/**
	 * @return the connectionTimeout
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * @return the refuseCookies
	 */
	public boolean isRefuseCookies() {
		return refuseCookies;
	}

	/**
	 * @return the redirect
	 */
	public boolean isRedirect() {
		return redirect;
	}

	/**
	 * @return the javascriptEnabled
	 */
	public boolean isJavascriptEnabled() {
		return javascriptEnabled;
	}

	/**
	 * @return the validateJavascript
	 */
	public boolean isValidateJavascript() {
		return validateJavascript;
	}

	/**
	 * @return the ignoreResponseCode
	 */
	public boolean isIgnoreResponseCode() {
		return ignoreResponseCode;
	}

	/**
	 * @return the autoRefresh
	 */
	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	/**
	 * @return the logIncorrectCode
	 */
	public boolean isLogIncorrectCode() {
		return logIncorrectCode;
	}
}