/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import groovy.util.BuilderSupport;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.inject.Injector;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.config.StackedScope;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.core.event.AfterModuleEvent;
import com.mgmtp.jfunk.core.event.BeforeModuleEvent;
import com.mgmtp.jfunk.core.event.ModuleInitializedEvent;
import com.mgmtp.jfunk.core.module.TestModuleImpl;
import com.mgmtp.jfunk.core.step.base.Step;

/**
 * Enables script modules in Groovy scripts.
 * 
 * @version $Id$
 */
@ScriptScoped
final class ModuleBuilder extends BuilderSupport {
	private final Logger log = Logger.getLogger(ScriptContext.class);

	private final Deque<TestModuleImpl> moduleStack = new ArrayDeque<TestModuleImpl>(2);

	private final EventBus eventBus;
	private final StackedScope moduleScope;
	private final Injector injector;

	@Inject
	ModuleBuilder(final EventBus eventBus, final StackedScope moduleScope, final Injector injector) {
		this.eventBus = eventBus;
		this.moduleScope = moduleScope;
		this.injector = injector;
	}

	@Override
	protected Object doInvokeMethod(final String methodName, final Object name, final Object args) {
		boolean isModule = "module".equals(methodName);
		Throwable throwable = null;

		try {
			return super.doInvokeMethod(methodName, name, args);
		} catch (ModuleExecutionException ex) {
			throwable = ex;

			// already handled, so just re-throw
			throw ex;
		} catch (AssertionError err) {
			throwable = err;

			handleThrowable("Assertion failed in module: ", isModule, err);
			throw err;
		} catch (RuntimeException ex) {
			throwable = ex;

			handleThrowable("Exception executing module: ", isModule, ex);
			throw ex;
		} catch (Throwable ex) {
			// We have to catch throwable to make sure any error makes it into a test's archive
			throwable = ex;

			handleThrowable("Exception executing module: ", isModule, ex);
			// not nice but we cannot rethrow a Throwable
			throw new JFunkException(throwable);
		} finally {
			if (isModule) {
				try {
					TestModuleImpl module = moduleStack.pop();
					module.setError(throwable != null);
					eventBus.post(new AfterModuleEvent(module, throwable));
					eventBus.post(new InternalAfterModuleEvent(module, throwable));
				} finally {
					moduleScope.exitScope();
				}
			}
		}
	}

	private void handleThrowable(final String messagePrefix, final boolean isModule, final Throwable th) {
		if (isModule) {
			TestModuleImpl module = moduleStack.peek();

			// We need to log the exception here on module level,
			// so it makes it into the log file in the module's archive
			log.error(messagePrefix + module, th);

			// Wrap into ModuleExecutionException, so we know later that
			// we don't have to log it again.
			throw new ModuleExecutionException(module, th);
		}
	}

	@Override
	protected Object createNode(final Object name) {
		return createNode(name, null, null);
	}

	@Override
	protected Object createNode(final Object name, @SuppressWarnings("rawtypes") final Map attrs) {
		return createNode(name, attrs, null);
	}

	@Override
	protected Object createNode(final Object name, final Object value) {
		return createNode(name, null, value);
	}

	@Override
	public Object createNode(final Object name, @SuppressWarnings("rawtypes") final Map attrs, final Object value) {
		if ("module".equals(name)) {
			moduleScope.enterScope();
			ScriptModule scriptModule = new ScriptModule(value.toString(), (String) attrs.get("dataSetKey"));
			injector.injectMembers(scriptModule);
			scriptModule.setExecuting(true);
			moduleStack.push(scriptModule);
			eventBus.post(new ModuleInitializedEvent(scriptModule));
			eventBus.post(new InternalBeforeModuleEvent(scriptModule));
			eventBus.post(new BeforeModuleEvent(scriptModule));
			return scriptModule;
		} else if ("step".equals(name)) {
			checkArgument(value instanceof Step, "No step instance: " + value);
			return value;
		}
		throw new IllegalStateException("'step' or 'module' expected, but '" + name + "' was found.");
	}

	@Override
	protected Object postNodeCompletion(final Object parent, final Object node) {
		if (node instanceof ScriptModule) {
			final ScriptModule currentModule = (ScriptModule) node;
			checkState(currentModule == moduleStack.peek(), "Wrong module on stack");
			currentModule.setExecuting(false);
		} else if (node instanceof Step) {
			Step step = (Step) node;
			ScriptModule currentModule = (ScriptModule) parent;
			currentModule.executeStep(step);
		} else {
			throw new IllegalStateException("No Step or TestModule instance found.");
		}
		return node;
	}

	@Override
	protected void setParent(final Object parent, final Object child) {
		//
	}

	private static class ScriptModule extends TestModuleImpl {

		public ScriptModule(final String name, final String dataSetKey) {
			super(name, dataSetKey);
		}

		public void setExecuting(final boolean executing) {
			this.executing = executing;
		}

		@Override
		public void executeStep(final Step step) {
			super.executeStep(step);
		}
	}
}