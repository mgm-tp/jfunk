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
