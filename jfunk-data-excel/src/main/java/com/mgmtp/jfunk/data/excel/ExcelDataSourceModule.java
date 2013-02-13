/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.excel;

import org.apache.poi.ss.usermodel.DataFormatter;

import com.mgmtp.jfunk.data.BaseDataSourceModule;

/**
 * Guice module for the standard data sources.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class ExcelDataSourceModule extends BaseDataSourceModule {

	@Override
	protected void doConfigure() {
		bind(DataFormatter.class);
		bindDataSource("excel").to(ExcelDataSource.class);
	}
}
