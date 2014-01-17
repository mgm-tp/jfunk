/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
