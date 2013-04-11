/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Maps.transformEntries;
import static com.google.common.collect.Maps.transformValues;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Provider;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps.EntryTransformer;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * <p>
 * Creates a {@link Map} of {@link Capabilities} by driver type, i. e. it is possible to configure
 * different capabilitiesMap for different driver types ({@code chrome}, {@code firefox},
 * {@code remote}, etc.). Global capabilitiesMap are considered for all driver types.
 * Driver-type-specific capabilitiesMap override global ones.
 * </p>
 * <p>
 * List capabilitiesMap (e. g. {@code chrome.switches}) are supported. They must have a unique
 * integer index(after the capability name, separated by a period) which is used to identity them as
 * list capabilitiesMap. The index has otherwise not special meaning.
 * </p>
 * 
 * <b>Configuring global capabilitiesMap:</b>
 * 
 * <pre>
 * webdriver.capability.globalCapability = globalCapabilityValue
 * </pre>
 * 
 * <b>Configuring capabilitiesMap per driver type:</b>
 * 
 * <pre>
 * webdriver.firefox.capability.firefoxCapability = firefoxCapabilityValue
 * webdriver.firefox.capability.globalCapability = globalCapabilityValueOverride
 * 
 * webdriver.remote.capability.stringCapability = stringCapabilityValue
 * webdriver.remote.capability.chrome.switches.1 = --window-size=1200,1200
 * webdriver.remote.capability.chrome.switches.2 = --window-position=-1210,0
 * </pre>
 * 
 * <b>Configuring a proxy</b>
 * 
 * <pre>
 * webdriver.proxy.httpProxy=localhost:8080
 * </pre>
 * 
 * All properties the class {@link Proxy} supports may be used (prefixed by {@code webdriver.proxy}
 * ). Currently, these are:
 * 
 * <pre>
 * proxyType
 * ftpProxy
 * httpProxy
 * httpsProxy
 * noProxy
 * sslProxy
 * socksProxy
 * socksUsername
 * socksPassword
 * proxyAutoconfigUrl
 * autodetect
 * </pre>
 * 
 * @author rnaegele
 */
public class CapabilitiesProvider implements Provider<Map<String, Capabilities>> {

	private static final String PROXY_PREFIX = "webdriver.proxy.";

	private static final Pattern CAPABILITIES_PREFIX_PATTERN = Pattern.compile("webdriver[.](?:([^.]+)[.])?capability");

	private final Provider<Configuration> configProvider;

	@Inject
	protected CapabilitiesProvider(final Provider<Configuration> configProvider) {
		this.configProvider = configProvider;
	}

	@Override
	public Map<String, Capabilities> get() {
		Configuration config = configProvider.get();

		Map<String, Map<String, List<JFunkCapability>>> capabilitiesMap = newHashMap();
		for (Entry<String, String> entry : config.entrySet()) {
			String key = entry.getKey();
			Matcher matcher = CAPABILITIES_PREFIX_PATTERN.matcher(key);
			if (matcher.find()) {
				String driverType = matcher.groupCount() == 1 && matcher.group(1) != null ? matcher.group(1) : "global";
				String capabilityString = key.substring(matcher.end() + 1);
				int lastDotIndex = capabilityString.lastIndexOf('.');
				String value = entry.getValue();

				JFunkCapability capability;
				if (lastDotIndex != -1) {
					JFunkCapabilityType type = JFunkCapabilityType.LIST;
					try {
						Integer.parseInt(capabilityString.substring(lastDotIndex + 1));
						capabilityString = capabilityString.substring(0, lastDotIndex);
					} catch (NumberFormatException ex) {
						// not a list capability
						type = JFunkCapabilityType.STRING;
					}
					capability = new JFunkCapability(capabilityString, value, type);
				} else {
					capability = new JFunkCapability(capabilityString, value, JFunkCapabilityType.STRING);
				}

				Map<String, List<JFunkCapability>> map = capabilitiesMap.get(driverType);
				if (map == null) {
					map = newHashMapWithExpectedSize(5);
					capabilitiesMap.put(driverType, map);
				}
				List<JFunkCapability> list = map.get(capability.name);
				if (list == null) {
					list = newArrayListWithExpectedSize(1);
					map.put(capability.name, list);
				}
				list.add(capability);
			}
		}

		Map<String, List<JFunkCapability>> tmpGlobals = capabilitiesMap.remove("global");
		final Map<String, Object> globalCapabilities = tmpGlobals == null
				? ImmutableMap.<String, Object>of()
				: transformCapabilities(tmpGlobals);

		final Proxy proxy = createProxyFromConfig(config);

		// transform in to map of capabilities for each webdriver type
		final Map<String, Capabilities> byDriverTypeCapabilities = transformEntries(capabilitiesMap,
				new EntryTransformer<String, Map<String, List<JFunkCapability>>, Capabilities>() {
					@Override
					public Capabilities transformEntry(final String key, final Map<String, List<JFunkCapability>> value) {
						Map<String, Object> capabilities = newHashMap(globalCapabilities);
						Map<String, Object> transformedCapabilities = transformCapabilities(value);
						capabilities.putAll(transformedCapabilities);

						DesiredCapabilities result = new DesiredCapabilities(capabilities);
						if (proxy != null) {
							result.setCapability(CapabilityType.PROXY, proxy);
						}
						return result;
					}
				});

		// wrap, so we get empty capabilities instead of nulls
		return new ForwardingMap<String, Capabilities>() {
			@Override
			protected Map<String, Capabilities> delegate() {
				return byDriverTypeCapabilities;
			}

			@Override
			public Capabilities get(final Object key) {
				Capabilities capabilities = super.get(key);
				if (capabilities == null) {
					DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
					if (proxy != null) {
						desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
					}
					capabilities = desiredCapabilities;
				}
				return capabilities;
			}
		};
	}

	private Proxy createProxyFromConfig(final Configuration config) {
		Map<String, String> proxyConfig = newHashMap();

		for (Entry<String, String> entry : config.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(PROXY_PREFIX)) {
				proxyConfig.put(substringAfter(key, PROXY_PREFIX), entry.getValue());
			}
		}

		if (proxyConfig.isEmpty()) {
			return null;
		}
		return new Proxy(proxyConfig);
	}

	private Map<String, Object> transformCapabilities(final Map<String, List<JFunkCapability>> capabilitiesMap) {
		return transformValues(capabilitiesMap, new Function<List<JFunkCapability>, Object>() {
			@Override
			public Object apply(final List<JFunkCapability> list) {
				if (list.size() == 1) {
					JFunkCapability capability = getOnlyElement(list);
					return capability.type == JFunkCapabilityType.LIST ? ImmutableList.of(capability.value) : capability.value;
				}
				return transform(list, new Function<JFunkCapability, String>() {
					@Override
					public String apply(final JFunkCapability capability) {
						return capability.value;
					}
				});
			}
		});
	}

	private static class JFunkCapability {
		private final String name;
		private final String value;
		private final JFunkCapabilityType type;

		public JFunkCapability(final String name, final String value, final JFunkCapabilityType type) {
			this.name = name;
			this.value = value;
			this.type = type;
		}
	}

	private static enum JFunkCapabilityType {
		STRING, LIST;
	}
}
