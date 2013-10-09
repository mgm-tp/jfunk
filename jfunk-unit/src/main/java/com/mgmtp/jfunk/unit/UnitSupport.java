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
package com.mgmtp.jfunk.unit;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.config.ThreadScope;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.ModulesLoader;
import com.mgmtp.jfunk.core.event.AfterRunEvent;
import com.mgmtp.jfunk.core.event.AfterScriptEvent;
import com.mgmtp.jfunk.core.event.BeforeRunEvent;
import com.mgmtp.jfunk.core.event.BeforeScriptEvent;
import com.mgmtp.jfunk.core.reporting.SimpleReporter;
import com.mgmtp.jfunk.core.scripting.ModuleExecutionException;
import com.mgmtp.jfunk.core.scripting.ScriptContext;

/**
 * Provides support for integrating jFunk into a unit test framework.
 * 
 * @author rnaegele
 */
class UnitSupport {

	private final Logger log = LoggerFactory.getLogger(getClass());

	static {
		SLF4JBridgeHandler.install();
	}

	static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

	@Inject
	EventBus eventBus;

	@Inject
	JFunkRunner jFunkRunner;

	@Inject
	Provider<ScriptContext> scriptContextProvider;

	@Inject
	ThreadScope scriptScope;

	private Injector injector;

	void init(final Object testClassInstance) {
		try {
			Class<? extends Object> testClass = testClassInstance.getClass();
			JFunkProps props = testClass.getAnnotation(JFunkProps.class);

			String propsFileName = props != null ? props.value() : JFunkConstants.JFUNK_PROPERTIES;
			Module module = ModulesLoader.loadModulesFromProperties(new UnitModule(), propsFileName);
			injector = Guice.createInjector(module);

			// load config only in order to set global properties as system properties
			// specifying "true" as the last parameter
			injector.getInstance(Configuration.class).load(JFunkConstants.SCRIPT_PROPERTIES, false);

			injector.injectMembers(this);

			eventBus.post(new BeforeRunEvent());
		} catch (Exception ex) {
			throw new IllegalStateException("Error initializing JFunkRunner", ex);
		}
	}

	void beforeTest(final Object testClassInstance) {
		injector.injectMembers(testClassInstance);
	}

	void beforeScript(final String methodName) {
		// Set thread name in order to avoid archive directory clashes,
		// since the thread name is part of the directory name and TestNG
		// names all its thread "TestNG"
		Thread.currentThread().setName(StringUtils.leftPad(String.valueOf(THREAD_COUNTER.incrementAndGet()), 2, '0'));

		if (!scriptScope.isScopeEntered()) {
			// already entered if this is the main thread
			scriptScope.enterScope();
		}
		jFunkRunner.load(JFunkConstants.SCRIPT_PROPERTIES, false);
		jFunkRunner.registerReporter(new SimpleReporter());
		jFunkRunner.set(JFunkConstants.UNIT_TEST_METHOD, methodName);
		eventBus.post(new BeforeScriptEvent(methodName));
	}

	void afterScript(final String methodName, final boolean success, final Throwable throwable) {
		try {
			if (throwable != null) {
				// Look up the cause hierarchy if we find a ModuleExecutionException.
				// We only need to log exceptions other than ModuleExecutionException because they
				// have already been logged and we don't want to pollute the log file any further.
				Throwable th = throwable.getCause();
				while (!(th instanceof ModuleExecutionException)) {
					if (th == null) {
						// log original throwable which was passed in!!!
						log.error("Error executing script: " + throwable.getMessage(), throwable);
						break;
					}
					th = th.getCause();
				}
			}

			eventBus.post(new AfterScriptEvent(methodName, success));
		} finally {
			scriptScope.exitScope();
		}
	}

	void afterTest() {
		eventBus.post(new AfterRunEvent());
	}
}