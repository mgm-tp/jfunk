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
package com.mgmtp.jfunk.web.util;

import static com.google.common.base.Preconditions.checkArgument;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import com.gargoylesoftware.htmlunit.WebClient;
import com.mgmtp.jfunk.web.JFunkHtmlUnitWebDriver;

/**
 * Provides {@link WebDriver} utility methods.
 * 
 * @author rnaegele
 */
public final class WebDriverUtils {

	private WebDriverUtils() {
		// don't allow instantiation
	}

	/**
	 * Returns the wrapped {@link WebDriver} instance if the given one is an
	 * {@link EventFiringWebDriver}.
	 * 
	 * @param webDriver
	 *            the {@link WebDriver} instance
	 * @return the wrapped {@link WebDriver} instance
	 */
	public static WebDriver getWrappedDriver(final WebDriver webDriver) {
		if (webDriver instanceof EventFiringWebDriver) {
			return ((EventFiringWebDriver) webDriver).getWrappedDriver();
		}

		return webDriver;
	}

	/**
	 * Returns {@code true} if the specified {@link WebDriver} is an {@link HtmlUnitDriver}. Before
	 * checking, this method calls {@link #getWrappedDriver(WebDriver)} on the given
	 * {@link WebDriver} instance.
	 * 
	 * @param webDriver
	 *            the web driver
	 * @return {@code true} if the specified {@link WebDriver} is an {@link HtmlUnitDriver}
	 */
	public static boolean isHtmlUnitDriver(final WebDriver webDriver) {
		WebDriver driver = getWrappedDriver(webDriver);
		return driver instanceof JFunkHtmlUnitWebDriver;
	}

	/**
	 * Returns {@code true} if the specified {@link WebDriver} is an {@link RemoteWebDriver}. Before
	 * checking, this method calls {@link #getWrappedDriver(WebDriver)} on the given
	 * {@link WebDriver} instance.
	 * 
	 * @param webDriver
	 *            the web driver
	 * @return {@code true} if the specified {@link WebDriver} is an {@link RemoteWebDriver}
	 */
	public static boolean isRemoteWebDriver(final WebDriver webDriver) {
		WebDriver driver = getWrappedDriver(webDriver);
		return driver instanceof RemoteWebDriver;
	}

	/**
	 * Gets the {@link WebClient} of the specified {@link WebDriver}. Calls
	 * {@link #getWrappedDriver(WebDriver)} on the given {@link WebDriver} and checks that it is a
	 * {@link JFunkHtmlUnitWebDriver}.
	 * 
	 * @param webDriver
	 *            the web driver
	 * @return the {@link WebClient}
	 * @throws IllegalArgumentException
	 *             if the specified {@link WebDriver} is no {@link JFunkHtmlUnitWebDriver}
	 */
	public static WebClient getHtmlUnitDriverWebClient(final WebDriver webDriver) {
		WebDriver driver = webDriver;
		driver = getWrappedDriver(driver);
		checkArgument(driver instanceof JFunkHtmlUnitWebDriver, "Specified WebDriver is no JFunkHtmlUnitDriver: " + driver);
		return ((JFunkHtmlUnitWebDriver) driver).getWebClient();
	}
}
