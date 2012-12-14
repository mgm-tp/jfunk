/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.samples;

import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;
import com.mgmtp.jfunk.core.reporting.EmailReporter;

/**
 * @author rnaegele
 * @version $Id$
 */
public class SamplesModule extends BaseJFunkGuiceModule {

	@Override
	protected void doConfigure() {
		bindGlobalReporter().to(EmailReporter.class);
	}

}
