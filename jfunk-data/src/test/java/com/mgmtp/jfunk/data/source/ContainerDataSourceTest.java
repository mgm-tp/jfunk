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
package com.mgmtp.jfunk.data.source;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.source.MockDataSources.Mock1DataSource;
import com.mgmtp.jfunk.data.source.MockDataSources.Mock2DataSource;

/**
 */
public class ContainerDataSourceTest {

	private ContainerDataSource ds;

	@BeforeClass
	public void setUp() {
		Provider<List<? extends DataSource>> dsProvider = Providers.<List<? extends DataSource>>of(Lists.newArrayList(new Mock1DataSource(
				new Configuration(Charsets.UTF_8)), new Mock2DataSource(new Configuration(Charsets.UTF_8))));
		ds = new ContainerDataSource(dsProvider);
	}

	@Test
	public void testDataSetsAvailable() {
		DataSet data1 = ds.getNextDataSet("test1");
		Assert.assertNotNull(data1);

		DataSet data2 = ds.getNextDataSet("test2");
		Assert.assertNotNull(data2);
	}

	@Test
	public void testDataSetNotAvailable() {
		DataSet data = ds.getNextDataSet("test3");
		Assert.assertNull(data);
	}

	@Test
	public void testCopyDataSet() {
		ds.getNextDataSet("test1");
		ds.copyDataSetKey("test1", "test1.edit");
		Map<String, DataSet> dataSets = ds.getCurrentDataSets();
		Assert.assertTrue(dataSets.containsKey("test1.edit"));
		Assert.assertNotNull(dataSets.get("test1.edit"));
	}
}