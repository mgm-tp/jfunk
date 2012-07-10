package com.mgmtp.jfunk.web;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.apache.http.auth.UsernamePasswordCredentials;
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
		Map<String, UsernamePasswordCredentials> credentialsMap = module.provideHtmlUnitCredentialsMap(config);

		assertEquals(credentialsMap.get("www.testuser.com"), new UsernamePasswordCredentials("testuser1", "secret"));
		assertEquals(credentialsMap.get("www.mgm-tp.com"), new UsernamePasswordCredentials("mgm", "mgmmgm"));
		assertEquals(2, credentialsMap.size());
	}
}
