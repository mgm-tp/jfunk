package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * @author rnaegele
 * @version $Id$
 */
public abstract class ModuleEvent extends AbstractBaseEvent {

	private final TestModule module;

	public ModuleEvent(final TestModule module) {
		this.module = module;
	}

	/**
	 * @return the module
	 */
	public TestModule getModule() {
		return module;
	}
}
