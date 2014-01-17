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
package com.mgmtp.jfunk.data.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;

/**
 * Unit test for {@link CsvDataSource}.
 * 
 */
public class CsvDataSourceTest {

	private CsvDataSource ds;

	/**
	 * This method is annotated with {@link BeforeMethod}, because we need a fresh data source for
	 * every test.
	 */
	@BeforeMethod
	public void setUp() throws IOException {
		Configuration config = new Configuration(Charsets.UTF_8);
		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-datasources.properties");
			config.load(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
		ds = new CsvDataSource(config);
	}

	@Test
	public void testNextDataSetFoo() {
		for (int i = 1; i <= 2; ++i) {
			DataSet data = ds.getNextDataSet("foo");

			Assert.assertTrue(data.hasValue("foo-field1"));
			Assert.assertTrue(data.hasValue("empty"));
			Assert.assertTrue(data.hasValue("empty-quoted"));
			Assert.assertTrue(data.hasValue("foo-field0"));

			Assert.assertEquals(data.getValue("foo-field1"), "teststring-" + i % 2);
			Assert.assertEquals(data.getValue("empty"), "");
			Assert.assertEquals(data.getValue("empty-quoted"), "");
			Assert.assertEquals(data.getValueAsInteger("foo-field0"), Integer.valueOf(i % 2));
		}
	}

	@Test
	public void testNextDataSetBar() {
		for (int i = 1; i <= 2; ++i) {
			DataSet data = ds.getNextDataSet("bar");

			Assert.assertTrue(data.hasValue("quoted-string"));
			Assert.assertTrue(data.hasValue("quoted-int"));
			Assert.assertTrue(data.hasValue("quote-in-field"));
			Assert.assertTrue(data.hasValue("unquoted"));

			Assert.assertEquals(data.getValue("quoted-string"), "dummy-" + i % 2);
			Assert.assertEquals(data.getValueAsInteger("quoted-int"), Integer.valueOf(i % 2));
			Assert.assertEquals(data.getValue("quote-in-field"), "test\"string-" + i % 2);
			Assert.assertEquals(data.getValue("unquoted"), "unquoted-" + i % 2);
		}
	}

	@Test
	public void testHasMoreData() {
		int i = 0;
		while (ds.hasMoreData("bar")) {
			ds.getNextDataSet("bar");
			++i;
		}
		Assert.assertEquals(i, 2);
		i = 0;
		while (ds.hasMoreData("foo")) {
			ds.getNextDataSet("foo");
			++i;
		}
		Assert.assertEquals(i, 2);
	}

	@Test
	public void testCopyDataSet() {
		ds.getNextDataSet("foo");
		ds.copyDataSetKey("foo", "foo.edit");
		Map<String, DataSet> dataSets = ds.getCurrentDataSets();
		Assert.assertTrue(dataSets.containsKey("foo.edit"));
		DataSet dataEdit = dataSets.get("foo.edit");
		Assert.assertTrue(dataEdit.hasValue("foo-field1"));
		Assert.assertEquals(dataEdit.getValue("foo-field1"), "teststring-1");
	}
}