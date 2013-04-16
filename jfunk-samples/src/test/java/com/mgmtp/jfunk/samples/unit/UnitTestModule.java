/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.samples.unit;

import com.mgmtp.jfunk.core.module.TestModuleImpl;

/**
 * @author rnaegele
 */
public class UnitTestModule extends TestModuleImpl {

	private final String searchTerm;

	UnitTestModule(final String searchTerm) {
		super("google", "google");
		this.searchTerm = searchTerm;
	}

	@Override
	protected void executeSteps() {
		executeStep(new UnitTestStep(searchTerm));
	}
}