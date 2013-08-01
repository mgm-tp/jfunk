package com.mgmtp.jfunk.web.event;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * @author rnaegele
 * @version $Id: $
 */
public class BeforeWebDriverCreationEvent {

	private final String webDriverKey;
	private final DesiredCapabilities capabilities;

	/**
	 * @param webDriverKey
	 *            the {@link WebDriver} key as configured
	 * @param capabilities
	 *            the capabilities for the {@link WebDriver} to be created; may be modified in an
	 *            event handler
	 */
	public BeforeWebDriverCreationEvent(final String webDriverKey, final DesiredCapabilities capabilities) {
		this.webDriverKey = webDriverKey;
		this.capabilities = capabilities;
	}

	/**
	 * @return the webDriverKey
	 */
	public String getWebDriverKey() {
		return webDriverKey;
	}

	/**
	 * @return the capabilities which may be modified in order to customize the {@link WebDriver} to
	 *         be created
	 */
	public DesiredCapabilities getCapabilities() {
		return capabilities;
	}
}
