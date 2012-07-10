package com.mgmtp.jfunk.web;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.RefreshHandler;
import com.gargoylesoftware.htmlunit.WaitingRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.google.common.collect.Lists;
import com.mgmtp.jfunk.web.htmlunit.JavaScriptAlert;
import com.mgmtp.jfunk.web.util.DumpFileCreator;

/**
 * 
 * @author rnaegele
 * @version $Id$
 */
public class JFunkHtmlUnitDriverImpl extends HtmlUnitDriver implements IncorrectnessListener, RefreshHandler, CredentialsProvider,
		AlertHandler, JFunkHtmlUnitWebDriver {

	private final Logger log = Logger.getLogger(getClass());

	protected final HtmlUnitWebDriverParams webDriverParams;
	protected final HtmlUnitSSLParams sslParams;
	protected final AjaxController ajaxController;
	protected final Map<String, UsernamePasswordCredentials> credentialsMap;

	protected HttpClient httpClient;

	protected final List<JavaScriptAlert> collectedAlerts = Lists.newArrayList();
	protected final RefreshHandler refreshHandler = new WaitingRefreshHandler();
	protected final Provider<DumpFileCreator> htmlFileCreatorProvider;
	protected final Provider<File> moduleArchiveDirProvider;
	protected final Provider<Set<WebWindowListener>> listenersProvider;

	protected JFunkHtmlUnitDriverImpl(final BrowserVersion browserVersion, final HtmlUnitWebDriverParams webDriverParams,
			final AjaxController ajaxController, final HtmlUnitSSLParams sslParams, final Map<String, UsernamePasswordCredentials> credentialsMap,
			final Provider<DumpFileCreator> htmlFileCreatorProvider, final Provider<File> moduleArchiveDirProvider,
			final Provider<Set<WebWindowListener>> listenersProvider) {

		super(browserVersion);

		this.webDriverParams = webDriverParams;
		this.sslParams = sslParams;
		this.ajaxController = ajaxController;
		this.credentialsMap = credentialsMap;
		this.htmlFileCreatorProvider = htmlFileCreatorProvider;
		this.moduleArchiveDirProvider = moduleArchiveDirProvider;
		this.listenersProvider = listenersProvider;

		setJavascriptEnabled(webDriverParams.isJavascriptEnabled());

		// cannot override modifyWebClient because it is called in the super class' constructor before our params are set
		configureWebClient(getWebClient());
	}

	protected final void configureWebClient(final WebClient client) {
		client.setTimeout(webDriverParams.getConnectionTimeout());
		client.getCookieManager().setCookiesEnabled(!webDriverParams.isRefuseCookies());
		client.setRedirectEnabled(webDriverParams.isRedirect());
		client.setThrowExceptionOnScriptError(webDriverParams.isValidateJavascript());
		client.setThrowExceptionOnFailingStatusCode(!webDriverParams.isIgnoreResponseCode());
		client.setAjaxController(ajaxController);

		client.setAlertHandler(this);
		client.setCredentialsProvider(this);
		client.setIncorrectnessListener(this);
		client.setRefreshHandler(this);

		for (WebWindowListener listener : listenersProvider.get()) {
			client.addWebWindowListener(listener);
		}

		HtmlUnitHttpWebConnection webConnection = createWebConnection(client);
		client.setWebConnection(webConnection);
		httpClient = new ImmutableHttpClient(webConnection.getHttpClient());
	}

	protected HtmlUnitHttpWebConnection createWebConnection(final WebClient client) {
		return new HtmlUnitHttpWebConnection(client, sslParams);
	}

	/**
	 * Returns an {@link ImmutableHttpClient} around HtmlUnit's {@link HttpClient}.
	 * 
	 * @return the httpClient
	 */
	@Override
	public HttpClient getHttpClient() {
		return httpClient;
	}

	@Override
	public WebClient getWebClient() { //NOSONAR
		return super.getWebClient();
	}

	/**
	 * Returns a list of JavaScript alerts collected by the {@link AlertHandler}.
	 */
	public List<JavaScriptAlert> getAlerts() {
		return collectedAlerts;
	}

	@Override
	public void handleAlert(final Page page, final String message) {
		collectedAlerts.add(new JavaScriptAlert(page, message));
	}

	@Override
	public void setCredentials(final AuthScope authscope, final Credentials credentials) {
		// nothing to do here, credentials are not cached
	}

	@Override
	public Credentials getCredentials(final AuthScope authscope) {
		String host = authscope.getHost();
		log.debug("Retrieving credentials for host " + host);

		UsernamePasswordCredentials credentials = credentialsMap.get(host);
		checkState(credentials != null, "No credentials found for host " + host);

		return credentials;
	}

	@Override
	public void clear() {
		// nothing to do here, credentials are not cached
	}

	@Override
	public void handleRefresh(final Page page, final URL url, final int seconds) throws IOException {
		if (webDriverParams.isAutoRefresh()) {
			log.info("Executing refresh for URL " + url);
			refreshHandler.handleRefresh(page, url, seconds);
		} else {
			log.info("Ignoring refresh for URL " + url);
		}
	}

	@Override
	public void notify(final String message, final Object origin) {
		if (webDriverParams.isLogIncorrectCode()) {
			log.warn("Found incorrect information in HTML code: " + message + ", caused by: " + origin);
		}
	}
}