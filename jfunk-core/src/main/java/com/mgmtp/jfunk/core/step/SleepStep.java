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
package com.mgmtp.jfunk.core.step;

import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * This step causes the current thread to sleep for a given number of seconds.
 * 
 */
public class SleepStep extends BaseStep {

	private final long seconds;

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