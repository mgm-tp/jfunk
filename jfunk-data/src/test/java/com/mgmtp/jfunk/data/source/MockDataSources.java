/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.source;

import java.util.Arrays;

import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;

/**
 * @version $Id$
 */
class MockDataSources {

	abstract static class BaseMockDataSource extends BaseDataSource {
		private final String testKey;
		private final String testValue;

		public BaseMockDataSource(final Configuration config, final String testKey, final String testValue) {
			super(config);
			this.testKey = testKey;
			this.testValue = testValue;
		}

		@Override
		public boolean hasMoreData(final String dataSetKey) {
			return true;
		}

		@Override
		protected DataSet getNextDataSetImpl(final String key) {
			if (!Arrays.asList("test1", "test2").contains(key)) {
				return null;
			}
			DataSet ds = new DefaultDataSet();
			ds.setValue(testKey, testValue);
			return ds;
		}

		@Override
		protected void doReset() {
			// nothing to be done here
		}
	}

	public static class Mock1DataSource extends BaseMockDataSource {
		public Mock1DataSource(final Configuration config) {
			super(config, "test1", "testvalue1");
		}
	}

	public static class Mock2DataSource extends BaseMockDataSource {
		public Mock2DataSource(final Configuration config) {
			super(config, "test2", "testvalue2");
		}
	}
}