/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base implementation for steps.
 * 
 */
public abstract class BaseStep implements Step {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final String name;

	/**
	 * Creates a new instance.
	 * 
	 * @param name
	 *            the step's name (if {@code null}, {@code getClass().getSimpleName()} is used)
	 */
	public BaseStep(final String name) {
		this.name = name == null ? getClass().getSimpleName() : name;
	}

	/**
	 * Creates a new instance with the return value of {@code getClass().getSimpleName()} as its
	 * name.
	 */
	public BaseStep() {
		this(null);
	}

	@Override
	public String getName() {
		return name;
	}
}