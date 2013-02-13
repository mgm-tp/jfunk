/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;

/**
 * @author rnaegele
 * @version $Id$
 */
public class EventLoggingModule extends BaseJFunkGuiceModule {

	@Override
	protected void doConfigure() {
		bindEventHandler().to(EventLoggingEventHandler.class);
	}

}
