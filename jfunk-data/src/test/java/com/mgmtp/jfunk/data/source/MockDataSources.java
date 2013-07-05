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

import java.util.Arrays;

import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;

/**
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