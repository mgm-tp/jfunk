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

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;

/**
 * Unit test for {@link ArchiveDataSource}.
 * 
 */
public class ArchiveDataSourceTest {

	@DataProvider(name = "dataSources")
	public Object[][] createDataSources() throws IOException {
		Configuration config = new Configuration(Charsets.UTF_8);
		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-datasources.properties");
			config.load(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
		ArchiveDataSource dsFromProps = new ArchiveDataSource(config);

		return new Object[][] { { dsFromProps } };
	}

	@Test(dataProvider = "dataSources")
	public void testNextDataSet(final DataSource ds) {
		DataSet data = ds.getNextDataSet("google");
		Assert.assertEquals(data.getValue("countryId"), ":2n");
		Assert.assertEquals(data.getValue("languageId"), ":8");
		Assert.assertEquals(data.getValue("searchTerm"), "red");
	}

	@Test(dataProvider = "dataSources")
	public void testHasMoreData(final DataSource ds) {
		Assert.assertTrue(ds.hasMoreData("countryId"));
		Assert.assertTrue(ds.hasMoreData("languageId"));
		Assert.assertTrue(ds.hasMoreData("searchTerm"));
	}
}