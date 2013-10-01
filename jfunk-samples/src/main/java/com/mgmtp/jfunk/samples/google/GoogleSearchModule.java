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
package com.mgmtp.jfunk.samples.google;

import com.mgmtp.jfunk.core.module.TestModuleImpl;
import com.mgmtp.jfunk.samples.google.step.SearchStep;

public class GoogleSearchModule extends TestModuleImpl {

	public GoogleSearchModule() {
		super("google");
	}

	@Override
	protected void executeSteps() {
		executeSteps(new SearchStep());
	}
}
