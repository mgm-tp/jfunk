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
package com.mgmtp.jfunk.common.util;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;

/**
 */
public class ConfigurationTest {

	@BeforeClass
	public void setUp() {
		System.setProperty("config.dir", "src/test/resources");
	}

	@Test
	public void testPreserveExisting() {
		Configuration config = new Configuration(Charsets.UTF_8);
		config.put("prop1", "oldProp1Value");
		config.put("prop2", "oldProp2Value");
		config.put("prop3", "prop3Value");

		assertEquals(config.get("prop1"), "oldProp1Value");
		assertEquals(config.get("prop2"), "oldProp2Value");
		assertEquals(config.get("prop3"), "prop3Value");

		config.load("configuration_test.properties", true);

		assertEquals(config.get("prop1"), "oldProp1Value");
		assertEquals(config.get("prop2"), "oldProp2Value");
		assertEquals(config.get("prop3"), "prop3Value");
		assertEquals(config.get("prop4"), "prop4Value");
	}

	@Test
	public void testDontPreserveExisting() {
		Configuration config = new Configuration(Charsets.UTF_8);
		config.put("prop1", "oldProp1Value");
		config.put("prop2", "oldProp2Value");
		config.put("prop3", "prop3Value");

		assertEquals(config.get("prop1"), "oldProp1Value");
		assertEquals(config.get("prop2"), "oldProp2Value");
		assertEquals(config.get("prop3"), "prop3Value");

		config.load("configuration_test.properties");

		assertEquals(config.get("prop1"), "newProp1Value");
		assertEquals(config.get("prop2"), "newProp2Value");
		assertEquals(config.get("prop3"), "prop3Value");
		assertEquals(config.get("prop4"), "prop4Value");
	}

	@Test
	public void testReloadExtraFile() {
		Configuration config = new Configuration(Charsets.UTF_8);
		config.load("configuration_test.properties");

		assertEquals(config.get("extraProp"), "extra1");
		assertEquals(config.get("extraExtraProp"), "extraExtra");

		config.put("extra", "extra2");

		assertEquals(config.get("extraProp"), "extra2");
		assertEquals(config.get("extraExtraProp"), "extraExtra");
	}
}