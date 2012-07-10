package com.mgmtp.jfunk.common.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * Guice scope which uses thread-local storage for Guice-managed objects. A scope context needs to
 * be explicitly entered ({@link #enterScope()}) and exited ({@link #exitScope()}). Calling
 * {@link #enterScope()} pushes a new scope map onto the internal thread-local stack. Scoped objects
 * are provided from the top-most map on the stack. Calling {@link #exitScope()} pops a map off the
 * stack.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class StackedScope implements Scope {
	private final Logger log = LoggerFactory.getLogger(getClass());

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
				return String.format("%s[%s]", unscoped, StackedScope.this);
			}
		};
	}

	/**
	 * Enters a new scope context for the current thread by pushing a {@link Map} for this context
	 * onto the internal stack.
	 */
	public void enterScope() {
		scopeStackCache.get().push(new HashMap<Key<?>, Object>());
		log.debug("Entered scope.");
	}

	/**
	 * Exists the scope context of the current thread by popping the context's map off the internal
	 * stack.
	 */
	public void exitScope() {
		scopeStackCache.get().pop();
		log.debug("Exited scope.");
	}

	public <T> void removeFromScope(final Key<T> key) {
		Map<Key<?>, Object> map = scopeStackCache.get().peek();
		Object object = map.remove(key);
		if (object != null) {
			log.debug("Removed object from scope cache: [key=, object={}]", key, object);
		} else {
			log.warn("Could not remove object with key '{}' from scope cache. No such object in cache.", key);
		}
	}

	/**
	 * Clean-up method which clears the whole thread-local storage for the current thread. the
	 * internal stack will be empty afterwards.
	 */
	public void cleanUp() {
		scopeStackCache.remove();
	}

	@Override
	public String toString() {
		return "ThreadScope";
	}
}