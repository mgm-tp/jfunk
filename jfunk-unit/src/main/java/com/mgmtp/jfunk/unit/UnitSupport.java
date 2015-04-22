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
package com.mgmtp.jfunk.unit;

import java.util.Date;
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
import com.mgmtp.jfunk.core.config.ModulesLoader;
import com.mgmtp.jfunk.core.event.AfterRunEvent;
import com.mgmtp.jfunk.core.event.AfterScriptEvent;
import com.mgmtp.jfunk.core.event.BeforeRunEvent;
import com.mgmtp.jfunk.core.event.BeforeScriptEvent;
import com.mgmtp.jfunk.core.reporting.SimpleReporter;
import com.mgmtp.jfunk.core.scripting.ModuleExecutionException;
import com.mgmtp.jfunk.core.scripting.ScriptMetaData;

/**
 * Provides support for integrating jFunk into a unit test framework.
 *
 * @author rnaegele
 */
class UnitSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(UnitSupport.class);

	static {
		SLF4JBridgeHandler.install();
	}

	private static volatile UnitSupport instance;

	private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

	private final JFunkRunner jFunkRunner;
	private final EventBus eventBus;
	private final ThreadScope scriptScope;
	private final Injector injector;

	private final Provider<ScriptMetaData> scriptMetaDataProvider;

	public static UnitSupport getInstance() {
		if (instance == null) {
			synchronized (UnitSupport.class) {
				if (instance == null) {
					try {
						String propsFileName = System.getProperty("jfunk.props.file", "jfunk.properties");
						Module module = ModulesLoader.loadModulesFromProperties(new UnitModule(), propsFileName);
						Injector injector = Guice.createInjector(module);

						instance = injector.getInstance(UnitSupport.class);
						instance.eventBus.post(new BeforeRunEvent());

						Runtime.getRuntime().addShutdownHook(new Thread() {
							@Override
							public void run() {
								instance.eventBus.post(new AfterRunEvent());
							}
						});
					} catch (Exception ex) {
						LOGGER.error("Error initializing Guice", ex);
						throw new ExceptionInInitializerError(ex);
					}
				}
			}
		}
		return instance;
	}

	@Inject
	UnitSupport(final JFunkRunner jFunkRunner, final EventBus eventBus, final ThreadScope scriptScope, final Injector injector,
			final Provider<ScriptMetaData> scriptMetaDataProvider) {
		this.jFunkRunner = jFunkRunner;
		this.eventBus = eventBus;
		this.scriptScope = scriptScope;
		this.injector = injector;
		this.scriptMetaDataProvider = scriptMetaDataProvider;
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

		ScriptMetaData scriptMetaData = scriptMetaDataProvider.get();
		scriptMetaData.setScriptName(methodName);
		scriptMetaData.setStartDate(new Date());

		eventBus.post(new BeforeScriptEvent(methodName));
	}

	void afterScript(final String methodName, final boolean success, final Throwable throwable) {
		try {
			if (throwable != null) {
				// Look up the cause hierarchy if we find a ModuleExecutionException.
				// We only need to log exceptions other than ModuleExecutionException because they
				// have already been logged and we don't want to pollute the log file any further.
				Throwable th = throwable;
				while (!(th instanceof ModuleExecutionException)) {
					if (th == null) {
						// log original throwable which was passed in!!!
						LOGGER.error("Error executing method: " + methodName, throwable);
						break;
					}
					th = th.getCause();
				}
			}

			ScriptMetaData scriptMetaData = scriptMetaDataProvider.get();
			scriptMetaData.setEndDate(new Date());
			scriptMetaData.setThrowable(throwable);

			eventBus.post(new AfterScriptEvent(methodName, success));
		} finally {
			scriptScope.exitScope();
		}
	}
}
