/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * @author rnaegele
 * @version $Id$
 */
public class AfterModuleEvent extends ModuleEvent {

	private final Throwable throwable;

	public AfterModuleEvent(final TestModule module) {
		this(module, null);
	}

	public AfterModuleEvent(final TestModule module, final Throwable throwable) {
		super(module);
		this.throwable = throwable;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return throwable == null;
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}
}
