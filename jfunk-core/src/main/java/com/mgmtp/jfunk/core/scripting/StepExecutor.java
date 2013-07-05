/*
 * Copyright (c) 2013 mgm technology partners GmbH
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
package com.mgmtp.jfunk.core.scripting;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Injector;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.core.event.AfterStepEvent;
import com.mgmtp.jfunk.core.event.BeforeStepEvent;
import com.mgmtp.jfunk.core.event.StepEvent;
import com.mgmtp.jfunk.core.step.base.Step;

/**
 * This class' executeStep method is responsible for calling a step's execute method and posting
 * {@link StepEvent}s to the {@link EventBus}. Member injection is perform on the {@link Step}
 * instance before execution.
 * 
 */
@Singleton
public class StepExecutor {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Injector injector;
	private final EventBus eventBus;

	@Inject
	public StepExecutor(final Injector injector, final EventBus eventBus) {
		this.injector = injector;
		this.eventBus = eventBus;
	}

	public void executeStep(final Step step) {
		executeStep(step, -1, true);
	}

	public void executeStep(final Step step, final boolean triggerEvents) {
		executeStep(step, -1, triggerEvents);
	}

	public void executeStep(final Step step, final int index) {
		executeStep(step, index, true);
	}

	public void executeStep(final Step step, final int index, final boolean triggerEvents) {
		// perform DI on step
		injector.injectMembers(step);
		Throwable throwable = null;

		try {
			if (triggerEvents) {
				eventBus.post(new InternalBeforeStepEvent(step, index));
				eventBus.post(new BeforeStepEvent(step, index));
			}
			step.execute();
		} catch (RuntimeException ex) {
			throwable = ex;

			if (handleThrowable(step, ex)) {
				throw ex;
			}
		} catch (AssertionError err) {
			throwable = err;

			if (handleThrowable(step, err)) {
				throw err;
			}
		} catch (Throwable th) {
			throwable = th;

			if (handleThrowable(step, th)) {
				// not nice but cannot re-throw a Throwable
				throw new JFunkException(th);
			}
		} finally {
			if (triggerEvents) {
				eventBus.post(new AfterStepEvent(step, index, throwable));
				eventBus.post(new InternalAfterStepEvent(step, index, throwable));
			}
		}
	}

	private boolean handleThrowable(final Step step, final Throwable th) {
		if (step.getClass().isAnnotationPresent(ContinueOnError.class)) {
			log.error("Exception executing step: " + step, th);
			return false;
		}
		return true;
	}
}