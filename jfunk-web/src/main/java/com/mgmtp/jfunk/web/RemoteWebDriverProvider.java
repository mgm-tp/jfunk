/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * RemoteWebDriverProvider
 * 
 * @date $LastChangedDate$
 * @revision $LastChangedRevision$
 */
public class RemoteWebDriverProvider extends BaseWebDriverProvider {

	private static final Pattern INDEX_PATTERN = Pattern.compile("(.+?)\\.\\d+");

	@Inject
	public RemoteWebDriverProvider(final Configuration config, final Set<WebDriverEventListener> eventListeners) {
		super(config, eventListeners);
	}

	@Override
	protected WebDriver createWebDriver() {
		String remoteWebDriverUrl = config.get(WebConstants.REMOTE_WEBDRIVER_URL, "");
		Validate.notBlank(remoteWebDriverUrl, "Property '%s' must be set in configuration", WebConstants.REMOTE_WEBDRIVER_URL);

		URL url;
		try {
			url = new URL(remoteWebDriverUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal remote web driver hub url: " + remoteWebDriverUrl);
		}

		Multimap<String, String> capabilitiesMultimap = ArrayListMultimap.create();
		for (Entry<String, String> entry : config.entrySet()) {
			String key = entry.getKey();
			String prefix = "webdriver.remote.capability.";
			if (key.startsWith(prefix)) {
				String capability = substringAfter(key, prefix);
				Matcher matcher = INDEX_PATTERN.matcher(capability);
				if (matcher.matches()) {
					capability = matcher.group(1);
				}
				String value = entry.getValue();
				capabilitiesMultimap.put(capability, value);
			}
		}

		DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
		for (String capability : capabilitiesMultimap.keySet()) {
			Collection<String> capabilities = capabilitiesMultimap.get(capability);
			if (capabilities.size() > 1) {
				desiredCapabilities.setCapability(capability, ImmutableList.copyOf(capabilities));
			} else {
				desiredCapabilities.setCapability(capability, getOnlyElement(capabilities));
			}
		}

		log.info("Starting remote web driver with capability: {}", desiredCapabilities);
		return new RemoteWebDriver(url, desiredCapabilities);
	}
}
