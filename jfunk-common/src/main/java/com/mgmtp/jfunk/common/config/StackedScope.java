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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * Guice scope which uses thread-local storage for Guice-managed objects. A scope context needs to
 * be explicitly entered ({@link #enterScope()}) and exited ({@link #exitScope()}). Calling
 * {@link #enterScope()} pushes a new scope map onto the internal thread-local stack. Scoped objects
 * are provided from the top-most map on the stack. Calling {@link #exitScope()} pops a map off the
 * stack.
 * 
 * @author rnaegele
 */
public class StackedScope extends BaseScope {
	private final ThreadLocal<Deque<Map<Key<?>, Object>>> scopeStackCache = new ThreadLocal<Deque<Map<Key<?>, Object>>>() {
		@Override
		protected Deque<Map<Key<?>, Object>> initialValue() {
			return new ArrayDeque<Map<Key<?>, Object>>();
		}
	};

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@Override
			public T get() {
				Deque<Map<Key<?>, Object>> stack = scopeStackCache.get();
				checkArgument(!stack.isEmpty(), "Scope stack is empty. Please call StackedScope.enterScope() first.");
				Map<Key<?>, Object> map = stack.peek();
				return getScopedObject(key, unscoped, map);
			}

			@Override
			public String toString() {
				return String.format("%s[%s]", unscoped, StackedScope.this);
			}
		};
	}

	/**
	 * Enters a new scope context for the current thread by pushing a {@link Map} for this context
	 * onto the internal stack.
	 */
	@Override
	public void enterScope() {
		scopeStackCache.get().push(new HashMap<Key<?>, Object>());
		log.debug("Entered scope.");
	}

	/**
	 * Exists the scope context of the current thread by popping the context's map off the internal
	 * stack.
	 */
	@Override
	public void exitScope() {
		Map<Key<?>, Object> scopeMap = scopeStackCache.get().peek();
		performDisposal(scopeMap);
		scopeStackCache.get().pop();
		log.debug("Exited scope.");
	}
}