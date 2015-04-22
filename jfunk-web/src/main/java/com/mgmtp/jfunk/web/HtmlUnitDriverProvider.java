/*
 * Copyright (c) 2015 mgm technology partners GmbH
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

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.http.client.CredentialsProvider;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.google.common.eventbus.EventBus;
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
			final HtmlUnitWebDriverParams webDriverParams, final HtmlUnitSSLParams sslParams,
			final BrowserVersion browserVersion, final AjaxController ajaxController,
			final Map<String, CredentialsProvider> credentialsProviderMap,
			final Provider<DumpFileCreator> htmlFileCreatorProvider,
			@ModuleArchiveDir final Provider<File> moduleArchiveDirProvider,
			final Provider<Set<WebWindowListener>> listenersProvider, final Map<String, DesiredCapabilities> capabilitiesMap,
			final EventBus eventBus) {
		super(config, eventListeners, capabilitiesMap, eventBus);
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
	protected WebDriver createWebDriver(final DesiredCapabilities capabilities) {
		Proxy proxy = capabilities != null ? (Proxy) capabilities.getCapability(CapabilityType.PROXY) : null;
		return new JFunkHtmlUnitDriverImpl(browserVersion, webDriverParams, ajaxController, sslParams,
				credentialsProviderMap, htmlFileCreatorProvider, moduleArchiveDirProvider, listenersProvider, proxy);
	}
}
