/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.samples.unit;

import javax.inject.Inject;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mgmtp.jfunk.core.module.ContainerModule;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.samples.google.GoogleModule;
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
				.addTestModule(new GoogleModule(), prepareDataCallback, null)
				.addTestModule(new GoogleModule(), prepareDataCallback, null)
				.build();

		jFunkRunner.run(testModule);
	}
}
