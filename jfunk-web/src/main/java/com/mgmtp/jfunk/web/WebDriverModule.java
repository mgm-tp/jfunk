package com.mgmtp.jfunk.web;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Sets.newTreeSet;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.CredentialsProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.web.util.DumpFileCreator;
import com.mgmtp.jfunk.web.util.FormInputHandler;
import com.mgmtp.jfunk.web.util.WebDriverTool;
import com.mgmtp.jfunk.web.util.WebElementFinder;

/**
 * @author rnaegele
 * @version $Id$
 */
public class WebDriverModule extends BaseWebDriverModule {

	private static final Pattern HOST_EXTRACTION_PATTERN =
			Pattern.compile("(?<=\\Q" + WebConstants.HTMLUNIT_CREDENTIALS_PREFIX + ".\\E).*(?=\\Q." + JFunkConstants.PASSWORD + "\\E)");

	@Override
	protected void doConfigureWebDriverModule() {
		// Create an extra binding for the WebDriverEventListener using a qualifier annotation.
		// The Multibinder for the event listener will then link to this binding so it may be overridden.
		// This is necessary because multibindings themselves cannot be overridden.
		bind(WebDriverEventListener.class).annotatedWith(DefaultWebDriverEventListener.class).to(JFunkWebDriverEventListener.class);
		bindWebDriverEventListener().to(Key.get(WebDriverEventListener.class, DefaultWebDriverEventListener.class));

		bind(BrowserVersion.class).toInstance(BrowserVersion.INTERNET_EXPLORER_8);
		bind(AjaxController.class).to(NicelyResynchronizingAjaxController.class);
		bind(DumpFileCreator.class);
		bind(WebDriverTool.class);

		bindWebDriver(WebConstants.WEBDRIVER_HTMLUNIT, HtmlUnitDriverProvider.class, ScriptScoped.class);
		bindWebDriver(WebConstants.WEBDRIVER_FIREFOX, FirefoxDriverProvider.class, ScriptScoped.class);
		bindWebDriver(WebConstants.WEBDRIVER_CHROME, ChromeDriverProvider.class, ScriptScoped.class);
		bindWebDriver(WebConstants.WEBDRIVER_INTERNET_EXPLORER, InternetExplorerDriverProvider.class, ScriptScoped.class);
		bindWebDriver(WebConstants.WEBDRIVER_REMOTE, RemoteWebDriverProvider.class, ScriptScoped.class);

		bindScriptScopedDisposable().to(WebDriverDisposable.class);
	}

	@Provides
	@ScriptScoped
	protected WebDriver provideWebDriver(final Configuration config, final Injector injector) {
		String webDriverKey = config.get(WebConstants.WEBDRIVER_KEY);
		checkNotNull(webDriverKey, "Property '%s' is null. Check your configuration.", WebConstants.WEBDRIVER_KEY);
		return injector.getInstance(Key.get(WebDriver.class, Names.named(webDriverKey)));
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

		boolean validateJavascript = config.getBoolean(WebConstants.HTMLUNIT_VALIDATE_JS, false);
		log.info(WebConstants.HTMLUNIT_VALIDATE_JS + "=" + validateJavascript);

		boolean ignoreResponseCode = config.getBoolean(WebConstants.HTMLUNIT_IGNORE_RESPONSECODE, false);
		log.info(WebConstants.HTMLUNIT_IGNORE_RESPONSECODE + "=" + ignoreResponseCode);

		boolean autoRefresh = config.getBoolean(WebConstants.HTMLUNIT_AUTO_REFRESH, true);
		log.info(WebConstants.HTMLUNIT_AUTO_REFRESH + "=" + autoRefresh);

		boolean logIncorrectCode = config.getBoolean(WebConstants.HTMLUNIT_LOG_INCORRECT_CODE, true);
		log.info(WebConstants.HTMLUNIT_LOG_INCORRECT_CODE + "=" + logIncorrectCode);

		return new HtmlUnitWebDriverParams(connectionTimeout, refuseCookies, redirect, javascriptEnabled, validateJavascript, ignoreResponseCode,
				autoRefresh, logIncorrectCode);
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
		Set<String> credentialKeys = newTreeSet(Maps.filterKeys(config, new Predicate<String>() {
			@Override
			public boolean apply(final String input) {
				return input.startsWith(WebConstants.HTMLUNIT_CREDENTIALS_PREFIX);
			}
		}).keySet());

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

	@Provides
	@ScriptScoped
	protected WebElementFinder provideWebElementFinder(final WebDriver webDriver, final Configuration config) {
		WebElementFinder wef = WebElementFinder.create().webDriver(webDriver);

		String value = trimToNull(config.get(WebConstants.WEF_ENABLED));
		if (value != null) {
			wef = wef.enabled(Boolean.parseBoolean(value));
		}
		value = trimToNull(config.get(WebConstants.WEF_DISPLAYED));
		if (value != null) {
			wef = wef.displayed(Boolean.parseBoolean(value));
		}
		value = trimToNull(config.get(WebConstants.WEF_SELECTED));
		if (value != null) {
			wef = wef.selected(Boolean.parseBoolean(value));
		}
		long timeout = config.getLong(WebConstants.WEF_TIMEOUT_SECONDS, 0L);
		if (timeout > 0L) {
			long sleepMillis = config.getLong(WebConstants.WEF_SLEEP_MILLIS, 0L);
			wef = sleepMillis > 0L ? wef.timeout(timeout, sleepMillis) : wef.timeout(timeout);
		}

		return wef;
	}

	@Provides
	@ScriptScoped
	protected FormInputHandler provideFormInputHandler(final WebDriver webDriver, final Configuration config) {
		FormInputHandler fih = FormInputHandler.create().webDriver(webDriver);

		String value = trimToNull(config.get(WebConstants.FIH_ENABLED));
		if (value != null) {
			fih = fih.enabled(Boolean.parseBoolean(value));
		}
		value = trimToNull(config.get(WebConstants.FIH_DISPLAYED));
		if (value != null) {
			fih = fih.displayed(Boolean.parseBoolean(value));
		}
		value = trimToNull(config.get(WebConstants.FIH_SELECTED));
		if (value != null) {
			fih = fih.selected(Boolean.parseBoolean(value));
		}
		long timeout = config.getLong(WebConstants.FIH_TIMEOUT_SECONDS, 0L);
		if (timeout > 0L) {
			long sleepMillis = config.getLong(WebConstants.FIH_SLEEP_MILLIS, 0L);
			fih = sleepMillis > 0L ? fih.timeout(timeout, sleepMillis) : fih.timeout(timeout);
		}

		return fih;
	}
}
