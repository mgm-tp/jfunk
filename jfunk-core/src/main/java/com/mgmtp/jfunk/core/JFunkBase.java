/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
package com.mgmtp.jfunk.core;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.core.event.AfterRunEvent;
import com.mgmtp.jfunk.core.event.BeforeRunEvent;

/**
 * Base class for JFunk.
 * 
 */
public abstract class JFunkBase {

	protected static final Logger LOG = Logger.getLogger(JFunkBase.class);

	private final EventBus eventBus;

	@Inject
	public JFunkBase(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * Special logger which logs the global start and stop time and both the script names and
	 * execution times from the threads.
	 */
	protected static final Logger RESULT_LOG = Logger.getLogger(JFunkConstants.RESULT_LOGGER);

	/**
	 * Executes the jFunk test. A thread pool ({@link ExecutorService}) is created with the number
	 * of configured threads, which handles concurrent script execution.
	 */
	public final void execute() throws Exception {
		eventBus.post(createBeforeRunEvent());
		try {
			doExecute();
		} finally {
			eventBus.post(createAfterRunEvent());
		}
	}

	protected BeforeRunEvent createBeforeRunEvent() {
		return new BeforeRunEvent();
	}

	protected AfterRunEvent createAfterRunEvent() {
		return new AfterRunEvent();
	}

	protected abstract void doExecute() throws Exception;
}