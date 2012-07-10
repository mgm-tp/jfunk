package com.mgmtp.jfunk.common.config;

import static org.testng.Assert.fail;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * Unit test for {@link ThreadScope}.
 * 
 * @version $Id$
 */
public class ThreadScopeTest {

	/**
	 * Tests with 10 threads that for a certain thread always the same instance is returned for the
	 * same key.
	 */
	@Test
	public void testThreadScope() throws InterruptedException {
		final ThreadScope scope = new ThreadScope();

		int size = 10;
		final ExecutorService execSrv = Executors.newCachedThreadPool();

		/*
		 * Barrier makes sure that all threads start at the same time and that we don't reuse
		 * threads from the pool.
		 */
		final CyclicBarrier barrier = new CyclicBarrier(size);

		List<Callable<Set<Object>>> tasks = Lists.newArrayList();
		for (int i = 0; i < size; ++i) {
			tasks.add(new Callable<Set<Object>>() {
				@Override
				public Set<Object> call() {
					try {
						// Wait for the barrier.
						barrier.await();

						scope.enterScope();
						Set<Object> values = Sets.newHashSet();

						// The same value (value1, value2, value3) must be returned each time.
						Object value1 = lookupValue(Object.class, scope);
						values.add(value1);
						Object value2 = lookupValue(Object.class, scope);
						values.add(value2);
						Object value3 = lookupValue(Object.class, scope);
						values.add(value3);

						// The same value (value1, value2, value3) must be returned each time.
						value1 = lookupValue(Integer.class, scope);
						values.add(value1);
						value2 = lookupValue(Integer.class, scope);
						values.add(value2);
						value3 = lookupValue(Integer.class, scope);
						values.add(value3);

						return values;
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					} finally {
						scope.exitScope();
					}
				}

				private <T> T lookupValue(final Class<T> clazz, final ThreadScope threadScope) {
					return threadScope.scope(Key.get(clazz), new Provider<T>() {
						@SuppressWarnings("unchecked")
						@Override
						public T get() {
							return (T) new Object();
						}
					}).get();
				}
			});
		}

		List<Future<Set<Object>>> result = execSrv.invokeAll(tasks);
		for (Future<Set<Object>> future : result) {
			try {
				/*
				 * Each set must contain two different object because we tried to look up two
				 * different objects multiple times per thread.
				 */
				Assert.assertEquals(future.get().size(), 2);
			} catch (ExecutionException ex) {
				Assert.fail(ex.getMessage(), ex);
			}
		}

		// We started ten threads, so we must have ten result sets in the list.
		Assert.assertEquals(result.size(), size);
	}

	/**
	 * Test that after a clean up the cache is empty.
	 */
	@Test
	public void testCleanUp() {
		ThreadScope scope = new ThreadScope();
		Provider<Object> prov = scope.scope(Key.get(Object.class), new Provider<Object>() {
			@Override
			public Object get() {
				return new Object();
			}
		});

		scope.enterScope();

		Object obj1 = prov.get();
		Object obj2 = prov.get();
		Assert.assertTrue(obj1 == obj2);

		scope.exitScope();

		try {
			prov.get();
			fail("Expected IllegalStateException not thrown.");
		} catch (IllegalStateException ex) {
			// expected
		}
	}

	@Test
	public void testToString() {
		ThreadScope scope = new ThreadScope();
		Provider<String> prov = scope.scope(Key.get(String.class), new Provider<String>() {
			@Override
			public String get() {
				return "dummy";
			}

			@Override
			public String toString() {
				return get();
			}
		});
		Assert.assertEquals(prov.toString(), "dummy[ThreadScope]");
	}
}