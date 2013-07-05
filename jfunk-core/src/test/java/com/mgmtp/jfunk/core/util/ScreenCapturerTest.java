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
package com.mgmtp.jfunk.core.util;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.mutable.MutableInt;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.inject.util.Providers;
import com.mgmtp.jfunk.core.event.AbstractBaseEvent;
import com.mgmtp.jfunk.core.event.AfterModuleEvent;
import com.mgmtp.jfunk.core.event.BeforeModuleEvent;
import com.mgmtp.jfunk.core.module.DummyModule;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class ScreenCapturerTest {

	private File testFileOrDir;

	@AfterMethod
	public void cleanUp() {
		deleteQuietly(testFileOrDir);
	}

	@DataProvider(name = "exception")
	private Object[][] createExceptionDataProvider() {
		return new Object[][] { { null }, { new Exception() } };
	}

	@Test(groups = "excludeFromCI")
	public void testCaptureScreen() throws IOException {
		testFileOrDir = File.createTempFile("jFunkScreenCapture", ".png");
		ScreenCapturer.captureScreen(testFileOrDir);
		assertThat(testFileOrDir).exists();
	}

	@Test(dataProvider = "exception", groups = "excludeFromCI")
	public void testHandleEvent(final Exception ex) {
		testFileOrDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
		MutableInt counter = new MutableInt();

		Set<Class<? extends AbstractBaseEvent>> eventClasses =
				ImmutableSet.<Class<? extends AbstractBaseEvent>>of(BeforeModuleEvent.class);
		ScreenCapturer capturer = new ScreenCapturer(Providers.of(testFileOrDir), eventClasses, Providers.of(counter), ex != null);

		// get value before it is incremented
		int c = counter.intValue();

		EventBus eventBus = new EventBus();
		eventBus.register(capturer);
		eventBus.post(new BeforeModuleEvent(new DummyModule()));
		eventBus.post(new AfterModuleEvent(new DummyModule(), ex));

		assertThat(new File(testFileOrDir, String.format("screenshots/%04d_BeforeModuleEvent.png", c))).exists();

		// no configured for screenshot

		if (ex == null) {
			assertThat(new File(testFileOrDir, String.format("screenshots/%04d_AfterModuleEvent.png", ++c))).doesNotExist();
			assertThat(new File(testFileOrDir, String.format("screenshots/%04d_AfterModuleEvent_error.png", ++c))).doesNotExist();
		} else {
			assertThat(new File(testFileOrDir, String.format("screenshots/%04d_AfterModuleEvent_error.png", ++c))).exists();
		}
	}
}