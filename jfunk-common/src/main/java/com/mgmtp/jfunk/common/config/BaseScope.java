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
package com.mgmtp.jfunk.common.config;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.mgmtp.jfunk.common.util.Disposable;

/**
 * Guice scope which uses thread-local storage for Guice-managed objects. A scope context needs to
 * be explicitly entered ({@link #enterScope()}) and exited ({@link #exitScope()}). Calling
 * {@link #enterScope()} pushes a new scope map onto the internal thread-local stack. Scoped objects
 * are provided from the top-most map on the stack. Calling {@link #exitScope()} pops a map off the
 * stack.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public abstract class BaseScope implements Scope {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	Map<Key<?>, Disposable<?>> disposables;

	@Inject
	EventBus eventBus;

	public abstract void enterScope();

	public abstract void exitScope();

	/**
	 * If already present, gets the object for the specified key from the scope map. Otherwise it is
	 * retrieved from the unscoped provider and stored in the scope map.
	 * 
	 * @param key
	 *            the key
	 * @param unscoped
	 *            the unscoped provider for creating the object
	 * @param scopeMap
	 *            the scope map
	 * @return the correctly scoped object
	 */
	protected <T> T getScopedObject(final Key<T> key, final Provider<T> unscoped, final Map<Key<?>, Object> scopeMap) {
		// ok, because we know what we'd put in before
		@SuppressWarnings("unchecked")
		T value = (T) scopeMap.get(key);
		if (value == null) {
			/*
			 * no cache instance present, so we use the one we get from the unscoped provider and
			 * add it to the cache
			 */
			value = unscoped.get();
			scopeMap.put(key, value);
		}
		return value;
	}

	/**
	 * Iterates over the entries of the specified map calling potentially registered
	 * {@link Disposable}s.
	 * 
	 * @param scopeMap
	 *            the scope map
	 */
	protected void performDisposal(final Map<Key<?>, Object> scopeMap) {
		for (Entry<Key<?>, Object> entry : scopeMap.entrySet()) {
			Key<?> key = entry.getKey();

			// warning can be safely suppressed, we always get a Disposable and
			// the type parameter <Object> does not hurt here at runtime
			@SuppressWarnings("unchecked")
			Disposable<Object> disposable = (Disposable<Object>) disposables.get(key);
			if (disposable != null) {
				disposable.dispose(entry.getValue());
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}