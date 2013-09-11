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

import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.reporting.ReportContext;

/**
 * Event class posted after module executions. This allows custom reports to add additional report data to the context.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class ModuleReportedEvent extends AbstractReportedEvent {

	private final TestModule testModule;

	/**
	 * @param testModule
	 *            the test module
	 * @param reportContext
	 *            the report context
	 */
	public ModuleReportedEvent(final TestModule testModule, final ReportContext reportContext) {
		super(reportContext);
		this.testModule = testModule;
	}

	/**
	 * Returns the test module that is reported. Make sure you do not keep a reference to the module in order to avoid creating a
	 * memory leak.
	 * 
	 * @return the testModule
	 */
	public TestModule getTestModule() {
		return testModule;
	}
}
