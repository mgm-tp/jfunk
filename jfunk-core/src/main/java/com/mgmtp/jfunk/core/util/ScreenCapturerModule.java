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
package com.mgmtp.jfunk.core.util;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;

import java.util.Set;

import javax.inject.Singleton;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.mgmtp.jfunk.common.config.ModuleScoped;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;
import com.mgmtp.jfunk.core.event.AbstractBaseEvent;

/**
 * Guice module for the {@link ScreenCapturer}.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class ScreenCapturerModule extends BaseJFunkGuiceModule {

	private static final String SCREENSHOT_EVENT_CLASSES_KEY = "screenshot.event.classes";
	private static final String SCREENSHOT_ON_ERROR_KEY = "screenshot.on.error";

	@Override
	protected void doConfigure() {
		install(new PrivateModule() {
			@Override
			protected void configure() {
				// use private module so the MutableInt is only available to the ScreenCapturer

				try {
					bind(MutableInt.class).toConstructor(MutableInt.class.getConstructor()).in(ModuleScoped.class);
				} catch (SecurityException ex) {
					throw new JFunkException("Cannot access constructor", ex);
				} catch (NoSuchMethodException ex) {
					throw new JFunkException("Constructor not found", ex);
				}

				bind(ScreenCapturer.class);

				// expose only SccreenCapturer
				expose(ScreenCapturer.class);
			}
		});

		bindEventHandler().to(ScreenCapturer.class);
	}

	@Provides
	@Singleton
	Set<Class<? extends AbstractBaseEvent>> provideEventClassesRegisteredForScreenshots(final Configuration config) {
		String classesString = config.get(SCREENSHOT_EVENT_CLASSES_KEY);
		if (isNullOrEmpty(classesString)) {
			return ImmutableSet.of();
		}

		return FluentIterable.from(asList(classesString.split("\\s*,\\s*")))
				.transform(new Function<String, Class<? extends AbstractBaseEvent>>() {
					@Override
					public Class<? extends AbstractBaseEvent> apply(final String className) {
						try {
							return Class.forName(className).asSubclass(AbstractBaseEvent.class);
						} catch (ClassNotFoundException ex) {
							throw new JFunkException(ex);
						}
					}
				})
				.toSet();
	}

	@Provides
	@CaptureScreenOnError
	@Singleton
	boolean provideCaptureScreenOnError(final Configuration config) {
		return config.getBoolean(SCREENSHOT_ON_ERROR_KEY, false);
	}
}
