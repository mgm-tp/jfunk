/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
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
