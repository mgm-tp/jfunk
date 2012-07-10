package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * @author rnaegele
 * @version $Id$
 */
public class BeforeModuleEvent extends ModuleEvent {

	public BeforeModuleEvent(final TestModule module) {
		super(module);
	}
}
