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
package com.mgmtp.jfunk.common.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * Guice scope which uses thread-local storage for Guice-managed objects. A scope context needs to
 * be explicitly entered ({@link #enterScope()}) and exited ({@link #exitScope()}).
 * 
 * @author rnaegele
 */
public class ThreadScope extends BaseScope {

	final ThreadLocal<Map<Key<?>, Object>> scopeCache = new ThreadLocal<Map<Key<?>, Object>>();

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@Override
			public T get() {
				Map<Key<?>, Object> map = scopeCache.get();
				checkState(map != null, "No scope map found for the current thread. Forgot to call enterScope()?");
				return getScopedObject(key, unscoped, map);
			}

			@Override
			public String toString() {
				return String.format("%s[%s]", unscoped, ThreadScope.this);
			}
		};
	}

	/**
	 * Enters a new scope context for the current thread setting a scope map to the internal
	 * {@link ThreadLocal}.
	 * 
	 * @throws IllegalStateException
	 *             if there is already a scope context for the current thread
	 */
	@Override
	public void enterScope() {
		checkState(scopeCache.get() == null, "Scope has already been entered. Forgot to call exitScope()?");
		Map<Key<?>, Object> scopeMap = newHashMap();
		scopeCache.set(scopeMap);
		log.debug("Entered scope.");
	}

	/**
	 * Checks whether the scope has been entered, i. e. there is a scope cache for the current
	 * thread.
	 * 
	 * @return {@code true} if the scope has been entered by the current thread.
	 */
	public boolean isScopeEntered() {
		return scopeCache.get() != null;
	}

	/**
	 * Exits the scope context for the current thread. Call this method after a thread is done in
	 * order to avoid memory leaks and to enable the thread to enter a new scope context again.
	 * 
	 * @throws IllegalStateException
	 *             if there is no scope context for the current thread
	 */
	@Override
	public void exitScope() {
		Map<Key<?>, Object> scopeMap = checkNotNull(scopeCache.get(),
				"No scope map found for the current thread. Forgot to call enterScope()?");
		performDisposal(scopeMap);
		scopeCache.remove();
		log.debug("Exited scope.");
	}
}