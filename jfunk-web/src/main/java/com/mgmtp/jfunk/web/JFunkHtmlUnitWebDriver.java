package com.mgmtp.jfunk.web;

import org.apache.http.client.HttpClient;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author rnaegele
 * @version $Id$
 */
public interface JFunkHtmlUnitWebDriver {

	WebClient getWebClient();

	HttpClient getHttpClient();
}
