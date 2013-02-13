/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
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
