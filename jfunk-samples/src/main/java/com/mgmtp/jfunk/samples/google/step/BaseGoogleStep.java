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
package com.mgmtp.jfunk.samples.google.step;

import static com.google.common.base.Preconditions.checkState;

import com.mgmtp.jfunk.core.config.InjectConfig;
import com.mgmtp.jfunk.web.step.ComplexWebDriverStep;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public abstract class BaseGoogleStep extends ComplexWebDriverStep {

	@InjectConfig(name = "google.url")
	private String googleUrl;

	public BaseGoogleStep() {
		super("google");
	}

	@Override
	protected void executeSteps() {
		wdt.get(googleUrl);

		doExecuteSteps();

		String searchTerm = getDataSet().getValue("searchTerm");
		checkState(webDriver.getPageSource().contains(searchTerm), "Search '%s' term not contain in search result.", searchTerm);
	}

	protected abstract void doExecuteSteps();
}
