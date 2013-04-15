/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.http.client.CredentialsProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.ModuleArchiveDir;
import com.mgmtp.jfunk.web.util.DumpFileCreator;

/**
 * @author rnaegele
 */
public class HtmlUnitDriverProvider extends BaseWebDriverProvider {
	protected final HtmlUnitWebDriverParams webDriverParams;
	protected final HtmlUnitSSLParams sslParams;
	protected final BrowserVersion browserVersion;
	protected final AjaxController ajaxController;
	protected final Map<String, CredentialsProvider> credentialsProviderMap;
	protected final Provider<DumpFileCreator> htmlFileCreatorProvider;
	protected final Provider<File> moduleArchiveDirProvider;
	protected final Provider<Set<WebWindowListener>> listenersProvider;

	@Inject
	protected HtmlUnitDriverProvider(final Configuration config, final Set<WebDriverEventListener> eventListeners,
			final HtmlUnitWebDriverParams webDriverParams, final HtmlUnitSSLParams sslParams, final BrowserVersion browserVersion,
			final AjaxController ajaxController, final Map<String, CredentialsProvider> credentialsProviderMap,
			final Provider<DumpFileCreator> htmlFileCreatorProvider, @ModuleArchiveDir final Provider<File> moduleArchiveDirProvider,
			final Provider<Set<WebWindowListener>> listenersProvider) {
		super(config, eventListeners);
		this.webDriverParams = webDriverParams;
		this.sslParams = sslParams;
		this.browserVersion = browserVersion;
		this.ajaxController = ajaxController;
		this.credentialsProviderMap = credentialsProviderMap;
		this.htmlFileCreatorProvider = htmlFileCreatorProvider;
		this.moduleArchiveDirProvider = moduleArchiveDirProvider;
		this.listenersProvider = listenersProvider;
	}

	@Override
	protected WebDriver createWebDriver() {
		return new JFunkHtmlUnitDriverImpl(browserVersion, webDriverParams, ajaxController, sslParams,
				credentialsProviderMap, htmlFileCreatorProvider, moduleArchiveDirProvider, listenersProvider);
	}
}
