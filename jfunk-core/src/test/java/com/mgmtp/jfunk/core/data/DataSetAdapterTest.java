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
package com.mgmtp.jfunk.core.data;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.inject.util.Providers;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.source.DataSource;

/**
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
			closeQuietly(is);
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