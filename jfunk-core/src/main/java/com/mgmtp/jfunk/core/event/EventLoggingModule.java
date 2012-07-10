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
