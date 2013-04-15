/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step;

import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * This step causes the current thread to sleep for a given number of seconds.
 * 
 */
public class SleepStep extends BaseStep {

	private final long seconds;

	@Deprecated
	public SleepStep(final long seconds, @SuppressWarnings("unused") final TestModule test) {
		this(seconds);
	}

	public SleepStep(final long seconds) {
		this.seconds = seconds;
	}

	@Override
	public void execute() throws StepException {
		try {
			log.info("Sleeping for {} s", seconds);
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			log.error(ex.getMessage(), ex);
		}
	}
}