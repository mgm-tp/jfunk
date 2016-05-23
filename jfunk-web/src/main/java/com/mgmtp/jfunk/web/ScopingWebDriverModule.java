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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Sets.newTreeSet;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.CredentialsProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.web.util.DumpFileCreator;
import com.mgmtp.jfunk.web.util.FormInputHandler;
import com.mgmtp.jfunk.web.util.WebDriverTool;
import com.mgmtp.jfunk.web.util.WebElementFinder;

/**
 * Base Guice module using the scope annotation passed in the constructor in order to correctly
 * scope {@link WebDriver}-related bindings.
 *
 * @author rnaegele
 * @since 3.1
 */
abstract class ScopingWebDriverModule extends BaseWebDriverModule {

	private static final Pattern HOST_EXTRACTION_PATTERN =
		Pattern.compile("(?<=\\Q" + WebConstants.HTMLUNIT_CREDENTIALS_PREFIX + ".\\E).*(?=\\Q." + JFunkConstants.PASSWORD
			+ "\\E)");

	private final Class<? extends Annotation> scopeAnnotationClass;

	ScopingWebDriverModule(final Class<? extends Annotation> scopeAnnotationClass) {
		this.scopeAnnotationClass = scopeAnnotationClass;
	}

	@Override
	protected void doConfigureWebDriverModule() {
		// Create an extra binding for the WebDriverEventListener using a qualifier annotation.
		// The Multibinder for the event listener will then link to this binding so it may be overridden.
		// This is necessary because multibindings themselves cannot be overridden.
		bind(WebDriverEventListener.class).annotatedWith(DefaultWebDriverEventListener.class).to(
			JFunkWebDriverEventListener.class);
		bindWebDriverEventListener().to(Key.get(WebDriverEventListener.class, DefaultWebDriverEventListener.class));

		bind(BrowserVersion.class).toInstance(BrowserVersion.BEST_SUPPORTED);
		bind(AjaxController.class).to(NicelyResynchronizingAjaxController.class);
		bind(DumpFileCreator.class);

		bindWebDriver(WebConstants.WEBDRIVER_HTMLUNIT, HtmlUnitDriverProvider.class);
		bindWebDriver(WebConstants.WEBDRIVER_FIREFOX, FirefoxDriverProvider.class);
		bindWebDriver(WebConstants.WEBDRIVER_CHROME, ChromeDriverProvider.class);
		bindWebDriver(WebConstants.WEBDRIVER_INTERNET_EXPLORER, InternetExplorerDriverProvider.class);
		bindWebDriver(WebConstants.WEBDRIVER_REMOTE, RemoteWebDriverProvider.class);

		bindDisposable(Key.get(WebDriver.class)).to(WebDriverDisposable.class);

		bind(new TypeLiteral<Map<String, DesiredCapabilities>>() {
			//
		}).toProvider(CapabilitiesProvider.class);

		bind(WebDriver.class).toProvider(WebDriverProvider.class).in(scopeAnnotationClass);
		bind(WebElementFinder.class).toProvider(WebElementFinderProvider.class).in(scopeAnnotationClass);
		bind(FormInputHandler.class).toProvider(FormInputHandlerProvider.class).in(scopeAnnotationClass);
		bind(WebDriverTool.class).in(scopeAnnotationClass);
	}

	@Provides
	protected HtmlUnitWebDriverParams provideHtmlUnitWebDriverParams(final Configuration config) {
		int connectionTimeout = config.getInteger(WebConstants.HTMLUNIT_CONNECTION_TIMEOUT, 300000);
		log.info(WebConstants.HTMLUNIT_CONNECTION_TIMEOUT + "=" + connectionTimeout);

		boolean refuseCookies = config.getBoolean(WebConstants.HTMLUNIT_REFUSE_COOKIES, false);
		log.info(WebConstants.HTMLUNIT_REFUSE_COOKIES + "=" + refuseCookies);

		boolean redirect = config.getBoolean(WebConstants.HTMLUNIT_REDIRECT, true);
		log.info(WebConstants.HTMLUNIT_REDIRECT + "=" + redirect);

		boolean javascriptEnabled = config.getBoolean(WebConstants.HTMLUNIT_ENABLE_JAVASCRIPT, true);
		log.info(WebConstants.HTMLUNIT_ENABLE_JAVASCRIPT + "=" + javascriptEnabled);

		boolean cssEnabled = config.getBoolean(WebConstants.HTMLUNIT_ENABLE_CSS, true);
		log.info(WebConstants.HTMLUNIT_ENABLE_CSS + "=" + cssEnabled);

		boolean validateJavascript = config.getBoolean(WebConstants.HTMLUNIT_VALIDATE_JS, false);
		log.info(WebConstants.HTMLUNIT_VALIDATE_JS + "=" + validateJavascript);

		boolean ignoreResponseCode = config.getBoolean(WebConstants.HTMLUNIT_IGNORE_RESPONSECODE, false);
		log.info(WebConstants.HTMLUNIT_IGNORE_RESPONSECODE + "=" + ignoreResponseCode);

		boolean autoRefresh = config.getBoolean(WebConstants.HTMLUNIT_AUTO_REFRESH, true);
		log.info(WebConstants.HTMLUNIT_AUTO_REFRESH + "=" + autoRefresh);

		boolean logIncorrectCode = config.getBoolean(WebConstants.HTMLUNIT_LOG_INCORRECT_CODE, true);
		log.info(WebConstants.HTMLUNIT_LOG_INCORRECT_CODE + "=" + logIncorrectCode);

		return new HtmlUnitWebDriverParams(connectionTimeout, refuseCookies, redirect, javascriptEnabled, cssEnabled,
			validateJavascript,
			ignoreResponseCode, autoRefresh, logIncorrectCode);
	}

	@Provides
	protected HtmlUnitSSLParams provideHtmlUnitSSLParams(final Configuration config) {
		String keyStore = config.get(JFunkConstants.JAVAX_NET_SSL_KEY_STORE);
		log.info(JFunkConstants.JAVAX_NET_SSL_KEY_STORE + "=" + keyStore);

		String keyStorePassword = config.get(JFunkConstants.JAVAX_NET_SSL_KEY_STORE_PASSWORD);
		log.info(JFunkConstants.JAVAX_NET_SSL_KEY_STORE_PASSWORD + "=" + keyStorePassword);

		String keyStoreType = config.get(JFunkConstants.JAVAX_NET_SSL_KEY_STORE_TYPE);
		log.info(JFunkConstants.JAVAX_NET_SSL_KEY_STORE_TYPE + "=" + keyStoreType);

		String trustStore = config.get(JFunkConstants.JAVAX_NET_SSL_TRUST_STORE);
		log.info(JFunkConstants.JAVAX_NET_SSL_TRUST_STORE + "=" + trustStore);

		String trustStorePassword = config.get(JFunkConstants.JAVAX_NET_SSL_TRUST_STORE_PASSWORD);
		log.info(JFunkConstants.JAVAX_NET_SSL_TRUST_STORE_PASSWORD + "=" + trustStorePassword);

		String trustStoreType = config.get(JFunkConstants.JAVAX_NET_SSL_TRUST_STORE_TYPE);
		log.info(JFunkConstants.JAVAX_NET_SSL_TRUST_STORE_TYPE + "=" + trustStoreType);

		return new HtmlUnitSSLParams(keyStore, keyStorePassword, keyStoreType, trustStore, trustStorePassword, trustStoreType);
	}

	@Provides
	protected Map<String, CredentialsProvider> provideHtmlUnitCredentialsProviderMap(final Configuration config) {
		Map<String, CredentialsProvider> result = newHashMapWithExpectedSize(1);

		// extract sorted credential keys
		Set<String> credentialKeys = newTreeSet(Maps.filterKeys(config, (Predicate<String>) input -> input.startsWith(WebConstants.HTMLUNIT_CREDENTIALS_PREFIX)).keySet());

		for (Iterator<String> it = credentialKeys.iterator(); it.hasNext();) {
			String key = it.next();

			Matcher matcher = HOST_EXTRACTION_PATTERN.matcher(key);
			checkState(matcher.find(), "Could not extract host from property: " + key);

			String host = matcher.group();
			String password = config.get(key);

			// as set is sorted, the next key is that for the username for the current host
			String username = config.get(it.next());

			DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
			credentialsProvider.addCredentials(username, password);
			result.put(host, credentialsProvider);
		}

		return result;
	}
}
