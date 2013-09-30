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
package com.mgmtp.jfunk.core.config;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.mgmtp.jfunk.common.util.Disposable;
import com.mgmtp.jfunk.core.event.EventHandlers;
import com.mgmtp.jfunk.core.reporting.Reporter;

/**
 * Base class for Guice modules in jFunk.
 * 
 */
public abstract class BaseJFunkGuiceModule extends AbstractModule {

	protected Multibinder<Object> eventHandlersBinder;

	protected MapBinder<Key<?>, Disposable<?>> disposablebBinder;
	protected Multibinder<Reporter> globalReportersBinder;

	@Override
	protected final void configure() {
		eventHandlersBinder = Multibinder.newSetBinder(binder(), Object.class, EventHandlers.class);
		disposablebBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<Key<?>>() {
			//
		}, new TypeLiteral<Disposable<?>>() {
			//
		});
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
	 * Binds a {@link Disposable} for script scope. The method {@link Disposable#dispose(Object)} is
	 * called on all bound {@link Disposable}s whenever a scope is exited.
	 * 
	 * @see Multibinder#addBinding()
	 * @return a binding build used to add a {@link Disposable}
	 */
	protected LinkedBindingBuilder<Disposable<?>> bindDisposable(final Key<?> key) {
		return disposablebBinder.addBinding(key);
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
