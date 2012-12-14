/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.config;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * Guice scope which uses thread-local storage for Guice-managed objects. A scope context needs to
 * be explicitly entered ({@link #enterScope()}) and exited ({@link #exitScope()}).
 * 
 * @author rnaegele
 * @version $Id$
 */
public class ThreadScope implements Scope {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final ThreadLocal<Map<Key<?>, Object>> scopeCache = new ThreadLocal<Map<Key<?>, Object>>();

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@Override
			public T get() {
				Map<Key<?>, Object> map = scopeCache.get();

				checkState(map != null, "No scope map found for the current thread. Forgot to call enterScope()?");

				// ok, because we know what we'd put in before
				T value = (T) map.get(key);
				if (value == null) {
					/*
					 * no cache instance present, so we use the one we get from the unscoped
					 * provider and add it to the cache
					 */
					value = unscoped.get();
					map.put(key, value);
				}
				return value;
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
	public void exitScope() {
		checkState(scopeCache.get() != null, "No scope map found for the current thread. Forgot to call enterScope()?");
		scopeCache.remove();
		log.debug("Exited scope.");
	}

	@Override
	public String toString() {
		return "ThreadScope";
	}
}