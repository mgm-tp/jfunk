/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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

		assertEquals(config.size(), 3);
		assertEquals(config.get("prop1"), "oldProp1Value");
		assertEquals(config.get("prop2"), "oldProp2Value");
		assertEquals(config.get("prop3"), "prop3Value");

		config.load("configuration_test.properties", true);

		assertEquals(config.size(), 6);
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

		assertEquals(config.size(), 3);
		assertEquals(config.get("prop1"), "oldProp1Value");
		assertEquals(config.get("prop2"), "oldProp2Value");
		assertEquals(config.get("prop3"), "prop3Value");

		config.load("configuration_test.properties");

		assertEquals(config.size(), 6);
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