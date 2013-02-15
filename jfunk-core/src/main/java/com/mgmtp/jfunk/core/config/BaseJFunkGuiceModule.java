/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.config;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.mgmtp.jfunk.core.event.EventHandlers;
import com.mgmtp.jfunk.core.reporting.Reporter;

/**
 * Base class for Guice modules in jFunk.
 * 
 * @version $Id$
 */
public abstract class BaseJFunkGuiceModule extends AbstractModule {

	protected Multibinder<Object> eventHandlersBinder;

	protected Multibinder<Reporter> globalReportersBinder;

	@Override
	protected final void configure() {
		eventHandlersBinder = Multibinder.newSetBinder(binder(), Object.class, EventHandlers.class);
		globalReportersBinder = Multibinder.newSetBinder(binder(), Reporter.class);
		doConfigure();
	}

	/**
	 * @see BaseJFunkGuiceModule#configure()
	 */
	protected abstract void doConfigure();

	/**
	 * Binds an event handler. All bound event handlers are registered with the internal
	 * {@link EventBus}.
	 * 
	 * @see Multibinder#addBinding()
	 * @return a binding build used to add event handlers
	 */
	protected LinkedBindingBuilder<Object> bindEventHandler() {
		return eventHandlersBinder.addBinding();
	}

	/**
	 * Binds a global {@link Reporter} under the specified key.
	 * 
	 * @see Multibinder#addBinding()
	 * @return a binding build used to add a {@link Reporter}
	 */
	protected LinkedBindingBuilder<Reporter> bindGlobalReporter() {
		return globalReportersBinder.addBinding();
	}
}
