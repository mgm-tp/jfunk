/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.module.TestModule;


/**
 * {@link InternalAfterModuleEvent} for internal package-local use only. Is posted before the
 * regular {@link InternalAfterModuleEvent}.
 * 
 * @author rnaegele
 */
class InternalAfterModuleEvent extends InternalModuleEvent {

	private final Throwable throwable;

	public InternalAfterModuleEvent(final TestModule module) {
		this(module, null);
	}

	public InternalAfterModuleEvent(final TestModule module, final Throwable throwable) {
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
