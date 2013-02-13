/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mgmtp.jfunk.common.util.ExtendedProperties.SimpleEntry;

/**
 * Unit test for {@link ExtendedProperties}.
 * 
 * @version $Id$
 */
public class ExtendedPropertiesTest {

	private static final String PROPS_STRING = "prop1=öäü~@€^°é\n" + "prop2=test\\\ntest\r\n " + "prop3\\==test\\\\\n  prop4\\\r\ntest ";
	private static final String DEF_PROPS_STRING = "def\\=Prop1\\: foo\ndefProp2\\  bar";
	private static final String MORE_PROPS_STRING = "moreProp=more\\\\Prop";

	private ExtendedProperties props;

	@Test
	public void testProperties() {
		assertProps(props);

		String newKeyValue = "prop1";
		String expectedOldValue = "öäü~@€^°é";
		String actualOldValue = props.put(newKeyValue, newKeyValue);
		Assert.assertEquals(actualOldValue, expectedOldValue);
		Assert.assertEquals(props.get(newKeyValue), newKeyValue);
	}

	@Test
	public void testPutAll() {
		ExtendedProperties newProps = new ExtendedProperties();
		newProps.putAll(props);
		assertProps(newProps);
	}

	@Test
	public void testSaveLoad() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		props.store(baos, "UTF-8", "Test Comment", false, true);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ExtendedProperties newProps = new ExtendedProperties();
		newProps.load(bais, "UTF-8");

		assertProps(newProps);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(props);
		oos.flush();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		ExtendedProperties newProps = (ExtendedProperties) ois.readObject();

		assertProps(newProps);
		Assert.assertTrue(props.equals(newProps));
		Assert.assertTrue(newProps.equals(props));
		Assert.assertEquals(props.hashCode(), newProps.hashCode());
	}

	@Test
	public void testToProperties() {
		Properties legacyProps = props.toProperties();

		Assert.assertEquals(legacyProps.size(), 7);
		Assert.assertEquals(legacyProps.getProperty("prop1"), "öäü~@€^°é");
		Assert.assertEquals(legacyProps.getProperty("prop2"), "testtest");
		Assert.assertEquals(legacyProps.getProperty("prop3="), "test\\");
		Assert.assertEquals(legacyProps.getProperty("prop4test"), "");
		Assert.assertEquals(legacyProps.getProperty("def=Prop1:"), "foo");
		Assert.assertEquals(legacyProps.getProperty("defProp2 "), "bar");
		Assert.assertEquals(legacyProps.getProperty("moreProp"), "more\\Prop");
	}

	@Test
	public void testClone() {
		ExtendedProperties clone = props.clone();
		assertProps(clone);
	}

	@Test
	public void testFromProperties() {
		Properties p = new Properties();
		p.setProperty("test1", "test1");
		p.setProperty("test2", "test2");
		p.setProperty("test3", "test3");

		ExtendedProperties extProps = ExtendedProperties.fromProperties(p);
		Assert.assertEquals(extProps.size(), 3);
		Assert.assertEquals(extProps.get("test1"), "test1");
		Assert.assertEquals(extProps.get("test2"), "test2");
		Assert.assertEquals(extProps.get("test3"), "test3");
	}

	@Test
	public void testKeySet() {
		Set<String> keySet = props.keySet();

		Assert.assertEquals(props.size(), 7);
		Assert.assertEquals(keySet.size(), 7);
		Assert.assertTrue(keySet.contains("defProp2 "));
		Assert.assertTrue(props.containsKey("defProp2 "));

		props.put("newProp", "foo");

		Assert.assertEquals(props.size(), 8);
		Assert.assertEquals(keySet.size(), 8);
		Assert.assertTrue(keySet.contains("newProp"));

		props.remove("newProp");

		Assert.assertEquals(props.size(), 7);
		Assert.assertEquals(keySet.size(), 7);
		Assert.assertTrue(!keySet.contains("newProp"));
		Assert.assertTrue(!props.containsKey("newProp"));

		keySet.remove("moreProp");

		Assert.assertEquals(props.size(), 6);
		Assert.assertEquals(keySet.size(), 6);
		Assert.assertTrue(!keySet.contains("moreProp"));
		Assert.assertTrue(!props.containsKey("moreProp"));

		keySet.clear();
		Assert.assertTrue(keySet.isEmpty());
		Assert.assertTrue(props.isEmpty());
	}

	@Test
	public void testKeySetIterator() {
		Iterator<String> iter = props.keySet().iterator();
		for (int i = 0; i < 7; ++i) {
			iter.next();
			iter.remove();
		}
		Assert.assertTrue(!iter.hasNext());
		Assert.assertTrue(props.isEmpty());
	}

	@Test
	public void testValues() {
		Collection<String> values = props.values();

		Assert.assertEquals(props.size(), 7);
		Assert.assertEquals(values.size(), 7);
		Assert.assertTrue(values.contains("öäü~@€^°é"));
		Assert.assertTrue(values.contains("testtest"));
		Assert.assertTrue(values.contains("test\\"));
		Assert.assertTrue(values.contains(""));
		Assert.assertTrue(values.contains("foo"));
		Assert.assertTrue(values.contains("bar"));
		Assert.assertTrue(values.contains("more\\Prop"));

		props.put("value", "value");
		Assert.assertTrue(values.contains("value"));

		props.remove("value");
		Assert.assertTrue(!values.contains("value"));

	}

	@Test
	public void testEntrySet() {
		SimpleEntry entry = new SimpleEntry("newProp", "foo");
		Set<Entry<String, String>> entrySet = props.entrySet();
		props.put("newProp", "foo");

		Assert.assertEquals(props.size(), 8);
		Assert.assertEquals(entrySet.size(), 8);
		Assert.assertTrue(entrySet.contains(entry));
		Assert.assertTrue(props.containsKey(entry.getKey()));

		props.remove("newProp");

		Assert.assertEquals(props.size(), 7);
		Assert.assertEquals(entrySet.size(), 7);
		Assert.assertTrue(!entrySet.contains(entry));
		Assert.assertTrue(!props.containsKey(entry.getKey()));

		entry = new SimpleEntry("moreProp", "more\\Prop");
		entrySet.remove(entry);

		Assert.assertEquals(props.size(), 6);
		Assert.assertEquals(entrySet.size(), 6);
		Assert.assertTrue(!entrySet.contains(entry));
		Assert.assertTrue(!props.containsKey(entry.getKey()));

		entrySet.clear();
		Assert.assertTrue(entrySet.isEmpty());
		Assert.assertTrue(props.isEmpty());
	}

	@Test
	public void testEntrySetIterator() {
		Iterator<Entry<String, String>> iter = props.entrySet().iterator();
		for (int i = 0; i < 7; ++i) {
			iter.next();
			iter.remove();
		}
		Assert.assertTrue(!iter.hasNext());
		Assert.assertTrue(props.isEmpty());
	}

	@Test
	public void testContainsKey() {
		Assert.assertTrue(props.containsKey("prop1"));
		Assert.assertTrue(props.containsKey("prop2"));
		Assert.assertTrue(props.containsKey("prop3="));
		Assert.assertTrue(props.containsKey("prop4test"));
		Assert.assertTrue(props.containsKey("def=Prop1:"));
		Assert.assertTrue(props.containsKey("defProp2 "));
		Assert.assertTrue(props.containsKey("moreProp"));
	}

	@Test
	public void testContainsValue() {
		Assert.assertTrue(props.containsValue("öäü~@€^°é"));
		Assert.assertTrue(props.containsValue("testtest"));
		Assert.assertTrue(props.containsValue("test\\"));
		Assert.assertTrue(props.containsValue(""));
		Assert.assertTrue(props.containsValue("foo"));
		Assert.assertTrue(props.containsValue("bar"));
		Assert.assertTrue(props.containsValue("more\\Prop"));
	}

	private void assertProps(final ExtendedProperties ep) {
		Assert.assertEquals(ep.size(), 7);
		Assert.assertEquals(ep.get("prop1"), "öäü~@€^°é");
		Assert.assertEquals(ep.get("prop2"), "testtest");
		Assert.assertEquals(ep.get("prop3="), "test\\");
		Assert.assertEquals(ep.get("prop4test"), "");
		Assert.assertEquals(ep.get("def=Prop1:"), "foo");
		Assert.assertEquals(ep.get("defProp2 "), "bar");
		Assert.assertEquals(ep.get("moreProp"), "more\\Prop");
	}

	@BeforeMethod
	void loadProps() throws IOException {
		ExtendedProperties localProps = new ExtendedProperties();
		localProps.load(new StringReader(MORE_PROPS_STRING));

		localProps = new ExtendedProperties(localProps);
		localProps.load(new StringReader(DEF_PROPS_STRING));

		localProps = new ExtendedProperties(localProps);
		localProps.load(new StringReader(PROPS_STRING));
		props = localProps;
	}
}