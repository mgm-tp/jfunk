/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.source;

import java.util.Map;
import java.util.Map.Entry;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.mgmtp.jfunk.data.DefaultDataSet;

/**
 * @version $Id$
 */
public class DefaultDataSetTest {

	private DefaultDataSet ds;

	@BeforeClass
	public void setUp() {
		Map<String, String> data = Maps.newHashMap();
		data.put("testString", "foo");
		data.put("testInteger", "42");
		data.put("testBooleanTrue", "true");
		data.put("testBooleanFalse", "false");
		data.put("testDouble", "0.98765");
		data.put("testFixed", "nonFixed");
		data.put("testIndexedFixed#42", "nonFixed");
		data.put("testNonNormalized", "foo                bar");
		data.put("testIndexed#1", "bar");
		data.put("testRemove", "foo");
		data.put("testRemoveIndexed#42", "foo");

		ds = new DefaultDataSet(data);
	}

	@Test
	public void testHasValue() {
		boolean result = ds.hasValue("testString");
		Assert.assertTrue(result);
	}

	@Test
	public void testHasIndexedValue() {
		boolean result = ds.hasValue("testIndexed", 1);
		Assert.assertTrue(result);
	}

	@Test
	public void testGetValue() {
		String s = ds.getValue("testString");
		Assert.assertEquals(s, "foo");

		s = ds.getValue("nonExistentValue");
		Assert.assertEquals(s, "");
	}

	@Test
	public void testGetIndexedValue() {
		String s = ds.getValue("testIndexed", 1);
		Assert.assertEquals(s, "bar");
	}

	@Test
	public void testGetValueAsInteger() {
		Integer i = ds.getValueAsInteger("testInteger");
		Assert.assertEquals(i, Integer.valueOf(42));

		i = ds.getValueAsInteger("nonExistentValue");
		Assert.assertNull(i);
	}

	@Test
	public void testGetValueAsBoolean() {
		boolean b = ds.getValueAsBoolean("testBooleanTrue");
		Assert.assertTrue(b);

		b = ds.getValueAsBoolean("testBooleanFalse");
		Assert.assertTrue(!b);

		b = ds.getValueAsBoolean("nonExistentValue");
		Assert.assertTrue(!b);
	}

	@Test
	public void testGetValueDouble() {
		Double d = ds.getValueAsDouble("testDouble");
		Assert.assertEquals(d, Double.valueOf(0.98765));

		d = ds.getValueAsDouble("nonExistentValue");
		Assert.assertNull(d);
	}

	@Test
	public void testFixedValue() {
		String s = ds.getValue("testFixed");
		Assert.assertEquals(s, "nonFixed");

		String expected = "fixed";
		ds.setFixedValue("testFixed", expected);

		s = ds.getValue("testFixed");
		Assert.assertEquals(s, expected);

		for (Entry<String, String> entry : ds.getDataView().entrySet()) {
			if (entry.getKey().equals("testFixed")) {
				Assert.assertEquals(entry.getValue(), expected);
				break;
			}
		}

		ds.resetFixedValues();

		s = ds.getValue("testFixed");
		Assert.assertEquals(s, "nonFixed");

		for (Entry<String, String> entry : ds.getDataView().entrySet()) {
			if (entry.getKey().equals("nonFixed")) {
				Assert.assertEquals(entry.getValue(), expected);
				break;
			}
		}
	}

	@Test
	public void testHasFixedValue() {
		String key = "invalidKey";
		Assert.assertFalse(ds.hasValue(key));
		ds.setFixedValue(key, "1");
		Assert.assertTrue(ds.hasValue(key));
		ds.resetFixedValues();
		Assert.assertFalse(ds.hasValue(key));

		key = "testFixed";
		Assert.assertTrue(ds.hasValue(key));
		ds.setFixedValue("testFixed", "1");
		Assert.assertTrue(ds.hasValue(key));
		ds.resetFixedValues();
		Assert.assertTrue(ds.hasValue(key));
	}

	@Test
	public void testSetValue() {
		String expected = "123456";
		ds.setValue("newValue", expected);
		String result = ds.getValue("newValue");

		Assert.assertEquals(result, expected);
	}

	@Test
	public void testRemove() {
		Assert.assertTrue(ds.hasValue("testRemove"));
		ds.removeValue("testRemove");
		Assert.assertTrue(!ds.hasValue("testRemove"));
	}

	@Test
	public void testRemoveIndexed() {
		Assert.assertTrue(ds.hasValue("testRemoveIndexed", 42));
		ds.removeValue("testRemoveIndexed", 42);
		Assert.assertTrue(!ds.hasValue("testRemoveIndexed", 42));
	}

	@Test
	public void testContainsKey() {
		Assert.assertTrue(ds.containsKey("testFixed"));
	}

	@Test
	public void testContainsKeyIndexed() {
		Assert.assertTrue(ds.containsKey("testIndexedFixed", 42));
	}
}