/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;

import com.gargoylesoftware.htmlunit.HttpWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.web.ssl.JFunkSSLSocketFactory;

/**
 * This {@link WebConnection} uses its own {@link LayeredSchemeSocketFactory} as the default
 * HttpClient implementation does not work with sites which require a client certificate.
 * 
 * @version $Id$
 */
public class HtmlUnitHttpWebConnection extends HttpWebConnection {

	private final String keyStore;
	private final String keyStorePassword;
	private final String keyStoreType;
	private final String trustStore;
	private final String trustStorePassword;
	private final String trustStoreType;

	public HtmlUnitHttpWebConnection(final WebClient webClient, final HtmlUnitSSLParams sslParams) {
		super(webClient);
		this.keyStore = sslParams.getKeyStore();
		this.keyStorePassword = sslParams.getKeyStorePassword();
		this.keyStoreType = sslParams.getKeyStoreType();
		this.trustStore = sslParams.getTrustStore();
		this.trustStorePassword = sslParams.getTrustStorePassword();
		this.trustStoreType = sslParams.getTrustStoreType();
	}

	@Override
	protected AbstractHttpClient createHttpClient() {
		AbstractHttpClient httpClient = super.createHttpClient();

		URL keyStoreUrl = null;
		URL trustStoreUrl = null;
		try {
			if (StringUtils.isNotBlank(keyStore)) {
				keyStoreUrl = new File(keyStore).toURI().toURL();
			}
			if (StringUtils.isNotBlank(trustStore)) {
				trustStoreUrl = new File(trustStore).toURI().toURL();
			}
		} catch (MalformedURLException e) {
			throw new JFunkException("Could not construct URL from file", e);
		}

		LayeredSchemeSocketFactory socketFactory = new JFunkSSLSocketFactory(keyStoreUrl, keyStorePassword, keyStoreType, trustStoreUrl,
				trustStorePassword, trustStoreType);

		ClientConnectionManager ccm = httpClient.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 443, socketFactory));

		return httpClient;
	}

	@Override
	protected synchronized AbstractHttpClient getHttpClient() {
		return super.getHttpClient();
	}
}