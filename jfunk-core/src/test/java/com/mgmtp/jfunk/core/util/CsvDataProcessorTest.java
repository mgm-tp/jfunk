/*
 * Copyright (c) 2015 mgm technology partners GmbH
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
package com.mgmtp.jfunk.core.util;

import static org.testng.Assert.assertEquals;

import java.io.StringReader;

import org.apache.commons.lang3.mutable.MutableInt;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.inject.util.Providers;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;
import com.mgmtp.jfunk.data.source.BaseDataSource;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * @author rnaegele
 */
public class CsvDataProcessorTest {

	private static final String CSV_LINES =
			"foo;test testKey1; test testKey2\n" +
					"foo0;newTestValue10;<auto>\n" +
					"foo1;newTestValue11;<auto>";

	@Test
	public void testCsvDataProcessor() {
		final Configuration config = new Configuration(Charsets.UTF_8);
		config.put("foo", "foovalue");

		final DataSource dataSource = new TestDataSource(config);

		CsvDataProcessor csvProc = new CsvDataProcessor(Providers.of(config), Providers.of(dataSource));
		final MutableInt counter = new MutableInt();

		csvProc.processFile(new StringReader(CSV_LINES), ";", '\0', new Runnable() {
			@Override
			public void run() {
				// generate does this
				dataSource.getNextDataSet("test");

				int counterValue = counter.intValue();
				assertEquals(config.get("foo"), "foo" + counterValue);
				assertEquals(dataSource.getCurrentDataSet("test").getValue("testKey1"), "newTestValue1" + counterValue);
				assertEquals(dataSource.getCurrentDataSet("test").getValue("testKey2"), "testValue2");

				// write some random fixed values, which should be overridden/reset from CSV values
				dataSource.setFixedValue("test", "testKey1", "blablubb");
				dataSource.setFixedValue("test", "testKey2", "blablubb");

				counter.increment();
			}
		});
	}

	public class TestDataSource extends BaseDataSource {

		public TestDataSource(final Configuration config) {
			super(config);
		}

		@Override
		public String getName() {
			return "test";
		}

		@Override
		protected DataSet getNextDataSetImpl(final String key) {
			DataSet ds = new DefaultDataSet();
			ds.setValue("testKey1", "testValue1");
			ds.setValue("testKey2", "testValue2");
			return ds;
		}

		@Override
		public boolean hasMoreData(final String dataSetKey) {
			return true;
		}

		@Override
		protected void doReset() {
			// nothing to be done here
		}
	}
}
