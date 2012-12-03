package com.mgmtp.jfunk.unit;

import java.util.List;
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
import com.mgmtp.jfunk.core.util.ConfigLoader;

/**
 * Provides support for integrating jFunk into a unit test framework.
 * 
 * @author rnaegele
 * @version $Id$
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
			List<Module> modules = ModulesLoader.loadModulesFromProperties(new UnitModule(), propsFileName);
			injector = Guice.createInjector(modules);

			// load config only in order to set global properties as system properties
			// specifiying "true" as the last parameter
			ConfigLoader.loadConfig(injector.getInstance(Configuration.class), JFunkConstants.SCRIPT_PROPERTIES, false, true);

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
		eventBus.post(new BeforeScriptEvent(null));
	}

	void afterScript(final boolean success, final Throwable throwable) {
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

			eventBus.post(new AfterScriptEvent(null, success));
		} finally {
			scriptScope.exitScope();
		}
	}

	void afterTest() {
		if (!scriptScope.isScopeEntered()) {
			// TODO this is a hack, see https://jira.mgm-tp.com/jira/browse/MQA-946
			scriptScope.enterScope();
		}
		eventBus.post(new AfterRunEvent());
	}
}