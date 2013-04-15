/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Exceptions thrown during test module execution are wrapped into a
 * {@link ModuleExecutionException}.
 * 
 * @author rnaegele
 */
public class ModuleExecutionException extends RuntimeException {

	private final TestModule module;

	/**
	 * Creates a new instance for the specified test module and cause.
	 * 
	 * @param module
	 *            the test module whose execution resulted in an exception
	 * @param cause
	 *            the cause
	 */
	public ModuleExecutionException(final TestModule module, final Throwable cause) {
		super(cause);
		this.module = module;
	}

	/**
	 * @return the module
	 */
	public TestModule getModule() {
		return module;
	}
}
