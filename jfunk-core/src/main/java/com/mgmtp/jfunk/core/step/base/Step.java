/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step.base;

import com.google.common.eventbus.EventBus;
import com.mgmtp.jfunk.common.util.NamedObject;
import com.mgmtp.jfunk.core.event.StepEvent;
import com.mgmtp.jfunk.core.scripting.StepExecutor;

/**
 * <p>
 * Interface that all steps of a test module must implement. A single step can contain a simple
 * action (like setting a single form value) or be more complex consisting of several steps.
 * </p>
 * <p>
 * Note that steps should always be executed using the {@link StepExecutor} because it performs
 * members injection on steps before execution and post {@link StepEvent} to the {@link EventBus}.
 * Normally one does not have to care about this because it is handled internally. This is only
 * relevant if a step is to be executed manually. If you think you need a {@link StepExecutor}, you
 * should probably think about using a {@link ComplexStep} instead.
 * </p>
 * 
 */
public interface Step extends NamedObject {

	/**
	 * Executes some logic.
	 */
	void execute();
}