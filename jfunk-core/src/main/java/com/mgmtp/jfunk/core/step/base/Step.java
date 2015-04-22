/*
 * Copyright (c) 2015 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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