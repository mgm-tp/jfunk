package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * @author rnaegele
 * @version $Id$
 */
public class ModuleInitializedEvent extends ModuleEvent {

	public ModuleInitializedEvent(final TestModule module) {
		super(module);
	}
}
