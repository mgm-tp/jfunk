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