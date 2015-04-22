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
package com.mgmtp.jfunk.samples.unit;

import javax.inject.Inject;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mgmtp.jfunk.core.module.ContainerModule;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.samples.google.GoogleAdvancedSearchModule;
import com.mgmtp.jfunk.unit.JFunkRunner;
import com.mgmtp.jfunk.unit.JFunkTestNGSupport;

/**
 * @author rnaegele
 * @since 3.1
 */
@Listeners(JFunkTestNGSupport.class)
public class ContainerModuleTest {

	@Inject
	JFunkRunner jFunkRunner;

	@Test
	public void testContainerModule() {
		jFunkRunner.set("archive.dir", "testruns/container");

		Runnable prepareDataCallback = new Runnable() {
			@Override
			public void run() {
				jFunkRunner.prepareNextDataSet("google");
			}
		};

		TestModule testModule = ContainerModule.forName("container")
				.addTestModule(prepareDataCallback, new GoogleAdvancedSearchModule())
				.addTestModule(prepareDataCallback, new GoogleAdvancedSearchModule())
				.build();

		jFunkRunner.run(testModule);
	}
}
