/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
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
