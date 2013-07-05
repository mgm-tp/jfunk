/*
 * Copyright (c) 2013 mgm technology partners GmbH
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
package com.mgmtp.jfunk.web;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.util.Providers;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * @author rnaegele
 */
public class CapabilitiesProviderTest {

	@Test
	public void testGet() throws IOException {
		Configuration config = new Configuration(Charsets.UTF_8);
		Reader reader = null;
		try {
			reader = Resources.newReaderSupplier(Resources.getResource("test_capabilities.properties"), Charsets.UTF_8)
					.getInput();
			config.load(reader);

			CapabilitiesProvider provider = new CapabilitiesProvider(Providers.of(config));
			Map<String, Capabilities> capabilitiesMap = provider.get();

			assertThat(capabilitiesMap).hasSize(2);
			assertThat(capabilitiesMap).containsKey("firefox");
			assertThat(capabilitiesMap).containsKey("remote");

			Capabilities capabilities = capabilitiesMap.get("firefox");
			assertThat(capabilities.getCapability("globalCapability")).isEqualTo("globalCapabilityValueOverride");
			assertThat(capabilities.getCapability("firefoxCapability")).isEqualTo("firefoxCapabilityValue");

			capabilities = capabilitiesMap.get("remote");
			assertThat(capabilities.getCapability("globalCapability")).isEqualTo("globalCapabilityValue");
			assertThat(capabilities.getCapability("stringCapability")).isEqualTo("stringCapabilityValue");
			assertThat(capabilities.getCapability("listCapability")).isInstanceOf(List.class);

			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) capabilities.getCapability("listCapability");
			assertThat(list).contains("listCapabilityValue1", "listCapabilityValue2");

			Object proxy = capabilities.getCapability(CapabilityType.PROXY);
			assertThat(proxy).isInstanceOf(Proxy.class);
			assertThat(((Proxy) proxy).getHttpProxy()).isEqualTo("localhost:8080");
		} finally {
			closeQuietly(reader);
		}
	}

	@Test
	public void testGetWithProxyOnly() {
		Configuration config = new Configuration(Charsets.UTF_8);
		String proxyString = "localhost:8080";
		config.put("webdriver.proxy.httpProxy", proxyString);

		CapabilitiesProvider provider = new CapabilitiesProvider(Providers.of(config));
		Capabilities capabilities = provider.get().get("anykey");
		Object proxy = capabilities.getCapability(CapabilityType.PROXY);

		assertThat(proxy).isInstanceOf(Proxy.class);
		assertThat(((Proxy) proxy).getHttpProxy()).isEqualTo(proxyString);
	}
}
