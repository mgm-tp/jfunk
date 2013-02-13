/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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
 * @version $Id$
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