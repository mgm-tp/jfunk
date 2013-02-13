/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.module;

import com.mgmtp.jfunk.core.reporting.CsvReporter;

/**
 * No work is done here. However, this module can be used to generate CSV files including generated
 * data (using a {@link CsvReporter}).
 * 
 * @version $Id$
 */
public class DummyModule extends TestModuleImpl {

	public DummyModule() {
		super("DummyModule", null);
	}
}