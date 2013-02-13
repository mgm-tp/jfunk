/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.data;

import java.io.IOException;
import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.google.inject.util.Providers;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * @version $Id$
 */
public class DataSetAdapterTest {

	private DataSetAdapter adapter;
	private DataSource dataSource;

	@BeforeClass
	public void setUp() throws IOException {
		dataSource = new TestDataSource();
		adapter = new DataSetAdapter(Providers.of(dataSource));
		Configuration config = new Configuration(adapter, Charsets.UTF_8);
		InputStream is = getClass().getResourceAsStream("/dataSetAdapterTest.properties");
		try {
			config.load(is);
		} finally {
			Closeables.closeQuietly(is);
		}
	}

	@Test
	public void testExistingValues() {
		dataSource.getNextDataSet("test");

		String testValue1 = adapter.get("test testKey1");
		Assert.assertEquals(testValue1, "testValue1");

		String testValue2 = adapter.get("test testKey2");
		Assert.assertEquals(testValue2, "testValue2");
	}

	@Test
	public void testNulls() {
		dataSource.getNextDataSet("test");

		String testValue1 = adapter.get("keyWithoutValue");
		Assert.assertNull(testValue1);

		String testValue2 = adapter.get("nonExistingDsKey foo");
		Assert.assertNull(testValue2);
	}
}