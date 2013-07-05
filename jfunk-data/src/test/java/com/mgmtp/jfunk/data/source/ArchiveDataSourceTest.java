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
		DataSet data = ds.getNextDataSet("login");
		Assert.assertEquals(data.getValue("kurzname"), "dNBaHßA-");
		Assert.assertEquals(data.getValue("wieselAccountId"), "1000258699");

		data = ds.getNextDataSet("lstb");
		Assert.assertEquals(data.getValue("freierTextBeanAktuell.name7"), "çØ«!Þáüf£ÍC³_tVÌiÑ3³IÏ©µò2ÔF#");
		Assert.assertEquals(data.getValue("zeitraumjahr"), "2008");
	}

	@Test(dataProvider = "dataSources")
	public void testHasMoreData(final DataSource ds) {
		Assert.assertTrue(ds.hasMoreData("anteilseignerprofil"));
		Assert.assertTrue(ds.hasMoreData("antragstk"));
		Assert.assertTrue(ds.hasMoreData("arbeitnehmerprofil"));
		Assert.assertTrue(ds.hasMoreData("benutzereinstellungen"));
		Assert.assertTrue(ds.hasMoreData("euzi"));
		Assert.assertTrue(ds.hasMoreData("euziprofil"));
		Assert.assertTrue(ds.hasMoreData("finanzamt"));
		Assert.assertTrue(ds.hasMoreData("fsak"));
		Assert.assertTrue(ds.hasMoreData("kapesta"));
		Assert.assertTrue(ds.hasMoreData("kennnummerprofil"));
		Assert.assertTrue(ds.hasMoreData("kstr"));
		Assert.assertTrue(ds.hasMoreData("login"));
		Assert.assertTrue(ds.hasMoreData("lsta"));
		Assert.assertTrue(ds.hasMoreData("lstb"));
		Assert.assertTrue(ds.hasMoreData("sadvf"));
		Assert.assertTrue(ds.hasMoreData("steuerkontoabfrage"));
		Assert.assertTrue(ds.hasMoreData("steuernummerprofil"));
		Assert.assertTrue(ds.hasMoreData("ustdv"));
		Assert.assertTrue(ds.hasMoreData("ustidnrprofil"));
		Assert.assertTrue(ds.hasMoreData("ustsv"));
		Assert.assertTrue(ds.hasMoreData("ustva"));
		Assert.assertTrue(ds.hasMoreData("verus"));
		Assert.assertTrue(ds.hasMoreData("zm"));
	}
}