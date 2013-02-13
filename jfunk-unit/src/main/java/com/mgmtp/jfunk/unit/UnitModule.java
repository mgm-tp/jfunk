/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
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
