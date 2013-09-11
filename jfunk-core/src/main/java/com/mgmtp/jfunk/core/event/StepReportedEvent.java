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
package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.reporting.ReportContext;
import com.mgmtp.jfunk.core.step.base.Step;

/**
 * Event class posted after step executions. This allows custom reports to add additional report data to the context.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class StepReportedEvent extends AbstractReportedEvent {

	private final Step step;

	/**
	 * @param step
	 *            the step
	 * @param reportContext
	 *            the report context
	 */
	public StepReportedEvent(final Step step, final ReportContext reportContext) {
		super(reportContext);
		this.step = step;
	}

	/**
	 * Returns the step that is reported. Make sure you do not keep a reference to the step in order to avoid creating a memory
	 * leak.
	 * 
	 * @return the step
	 */
	public Step getStep() {
		return step;
	}
}
