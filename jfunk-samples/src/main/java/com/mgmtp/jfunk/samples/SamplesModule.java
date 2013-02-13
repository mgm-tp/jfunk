/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
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
