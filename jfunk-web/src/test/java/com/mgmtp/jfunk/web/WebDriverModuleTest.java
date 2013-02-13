/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * @author rnaegele
 * @version $Id$
 */
public class WebDriverModuleTest {

	@Test
	public void testHtmlUnitCredentials() {
		Configuration config = new Configuration(Charsets.UTF_8);

		config.put(WebConstants.HTMLUNIT_CREDENTIALS_PREFIX + ".www.testuser.com." + JFunkConstants.USERNAME, "testuser1");
		config.put(WebConstants.HTMLUNIT_CREDENTIALS_PREFIX + ".www.testuser.com." + JFunkConstants.PASSWORD, "secret");

		config.put(WebConstants.HTMLUNIT_CREDENTIALS_PREFIX + ".www.mgm-tp.com." + JFunkConstants.USERNAME, "mgm");
		config.put(WebConstants.HTMLUNIT_CREDENTIALS_PREFIX + ".www.mgm-tp.com." + JFunkConstants.PASSWORD, "mgmmgm");

		WebDriverModule module = new WebDriverModule();
		Map<String, CredentialsProvider> credentialsMap = module.provideHtmlUnitCredentialsProviderMap(config);

		CredentialsProvider cp = credentialsMap.get("www.testuser.com");
		assertEquals(cp.getCredentials(AuthScope.ANY).getUserPrincipal().getName(), "testuser1");
		assertEquals(cp.getCredentials(AuthScope.ANY).getPassword(), "secret");

		cp = credentialsMap.get("www.mgm-tp.com");
		assertEquals(cp.getCredentials(AuthScope.ANY).getUserPrincipal().getName(), "mgm");
		assertEquals(cp.getCredentials(AuthScope.ANY).getPassword(), "mgmmgm");
	}
}
