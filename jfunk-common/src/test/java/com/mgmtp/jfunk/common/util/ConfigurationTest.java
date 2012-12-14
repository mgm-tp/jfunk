/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.util;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;

/**
 * @version $Id$
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

		Assert.assertEquals(config.size(), 3);
		Assert.assertEquals(config.get("prop1"), "oldProp1Value");
		Assert.assertEquals(config.get("prop2"), "oldProp2Value");
		Assert.assertEquals(config.get("prop3"), "prop3Value");

		config.load("configuration_test.properties", true);

		Assert.assertEquals(config.size(), 4);
		Assert.assertEquals(config.get("prop1"), "oldProp1Value");
		Assert.assertEquals(config.get("prop2"), "oldProp2Value");
		Assert.assertEquals(config.get("prop3"), "prop3Value");
		Assert.assertEquals(config.get("prop4"), "prop4Value");
	}

	@Test
	public void testDontPreserveExisting() {
		Configuration config = new Configuration(Charsets.UTF_8);
		config.put("prop1", "oldProp1Value");
		config.put("prop2", "oldProp2Value");
		config.put("prop3", "prop3Value");

		Assert.assertEquals(config.size(), 3);
		Assert.assertEquals(config.get("prop1"), "oldProp1Value");
		Assert.assertEquals(config.get("prop2"), "oldProp2Value");
		Assert.assertEquals(config.get("prop3"), "prop3Value");

		config.load("configuration_test.properties");

		Assert.assertEquals(config.size(), 4);
		Assert.assertEquals(config.get("prop1"), "newProp1Value");
		Assert.assertEquals(config.get("prop2"), "newProp2Value");
		Assert.assertEquals(config.get("prop3"), "prop3Value");
		Assert.assertEquals(config.get("prop4"), "prop4Value");
	}
}