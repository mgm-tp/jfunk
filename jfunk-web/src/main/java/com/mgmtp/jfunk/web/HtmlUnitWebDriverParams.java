/*
 * Copyright (c) 2013 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.jfunk.web;

/**
 * @author rnaegele
 */
public class HtmlUnitWebDriverParams {

	private final int connectionTimeout;
	private final boolean refuseCookies;
	private final boolean redirect;
	private final boolean javascriptEnabled;
	private final boolean cssEnabled;
	private final boolean validateJavascript;
	private final boolean ignoreResponseCode;
	private final boolean autoRefresh;
	private final boolean logIncorrectCode;

	public HtmlUnitWebDriverParams(final int connectionTimeout, final boolean refuseCookies, final boolean redirect, final boolean javascriptEnabled,
			final boolean cssEnabled, final boolean validateJavascript, final boolean ignoreResponseCode, final boolean autoRefresh,
			final boolean logIncorrectCode) {
		this.connectionTimeout = connectionTimeout;
		this.refuseCookies = refuseCookies;
		this.redirect = redirect;
		this.javascriptEnabled = javascriptEnabled;
		this.cssEnabled = cssEnabled;
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
	 * @return the cssEnabled
	 */
	public boolean isCssEnabled() {
		return cssEnabled;
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