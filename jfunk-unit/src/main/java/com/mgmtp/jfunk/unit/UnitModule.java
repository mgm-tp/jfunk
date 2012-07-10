package com.mgmtp.jfunk.unit;

import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;
import com.mgmtp.jfunk.core.config.JFunkBaseModule;

/**
 * Guice module for unit test support.
 * 
 * @author rnaegele
 * @version $Id$
 */
class UnitModule extends BaseJFunkGuiceModule {

	@Override
	protected void doConfigure() {
		install(new JFunkBaseModule());
		bind(JFunkRunner.class);
	}
}
