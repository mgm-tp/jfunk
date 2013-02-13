/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Abstract base implementation for steps.
 * 
 * @version $Id$
 */
public abstract class BaseStep implements Step {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final String name;

	@Deprecated
	protected TestModule test;

	/**
	 * Creates a new instance.
	 * 
	 * @param name
	 *            the step's name (if {@code null}, {@code getClass().getSimpleName()} is used)
	 */
	public BaseStep(final String name) {
		this(name, null);
	}

	/**
	 * Creates a new instance with the return value of {@code getClass().getSimpleName()} as its
	 * name.
	 */
	public BaseStep() {
		this(null, null);
	}

	/**
	 * 
	 * @param testModule
	 *            param no longer used
	 */
	@Deprecated
	public BaseStep(final TestModule testModule) {
		this(null, testModule);
	}

	@Deprecated
	public BaseStep(final String name, final TestModule testModule) {
		this.name = name == null ? getClass().getSimpleName() : name;
		this.test = testModule;
	}

	@Override
	public String getName() {
		return name;
	}
}