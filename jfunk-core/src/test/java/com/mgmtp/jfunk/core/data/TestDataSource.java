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
package com.mgmtp.jfunk.core.data;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;
import com.mgmtp.jfunk.data.source.BaseDataSource;

/**
 */
@Singleton
public class TestDataSource extends BaseDataSource {

	@Inject
	public TestDataSource() {
		super(new Configuration(Charsets.UTF_8));
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