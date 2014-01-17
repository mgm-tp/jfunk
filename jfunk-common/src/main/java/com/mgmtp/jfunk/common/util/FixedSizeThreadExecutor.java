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
package com.mgmtp.jfunk.common.util;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Lists;

/**
 * {@link ExecutorService} implementation that executes a fixed number of tasks at a time. Each task
 * is executed in a new {@link Thread thread}. Threads are not reused.
 * 
 */
public class FixedSizeThreadExecutor extends AbstractExecutorService {

	private enum RunState {
		STOP, RUNNING, SHUTDOWN, TERMINATED;
	}

	private final ThreadFactory threadFactory;
	private final BlockingQueue<Thread> threads;
	private final Queue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();
	private volatile RunState runState = RunState.RUNNING;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition termination = lock.newCondition();

	/**
	 * Creates a new instance using the specified number of threads.
	 * 
	 * @param threads
	 *            The number of threads to use
	 * @param threadFactory
	 *            The thread factory used to create threads
	 */
	public FixedSizeThreadExecutor(final int threads, final ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
		this.threads = new LinkedBlockingQueue<Thread>(threads);
	}

	@Override
	public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		lock.lock();
		try {
			for (;;) {
				if (runState == RunState.TERMINATED) {
					return true;
				}
				if (nanos <= 0) {
					return false;
				}
				nanos = termination.awaitNanos(nanos);
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isShutdown() {
		return runState != RunState.RUNNING;
	}

	@Override
	public boolean isTerminated() {
		return runState == RunState.TERMINATED;
	}

	@Override
	public void shutdown() {
		runState = RunState.SHUTDOWN;
	}

	@Override
	public List<Runnable> shutdownNow() {
		lock.lock();
		try {
			runState = RunState.STOP;
			for (Thread th : threads) {
				th.interrupt();
			}
			if (threads.isEmpty()) {
				runState = RunState.TERMINATED;
				termination.signalAll();
			}
			List<Runnable> result = Lists.newArrayList(queue);
			queue.clear();
			return result;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void execute(final Runnable command) {
		if (runState != RunState.RUNNING) {
			throw new IllegalStateException("Cannot add any further tasks.");
		}

		queue.offer(command);

		Thread th = threadFactory.newThread(new Runnable() {
			@Override
			public void run() {
				try {
					queue.poll().run();
				} finally {
					threads.remove(Thread.currentThread());
					maybeSetTerminated();
				}
			}
		});

		try {
			threads.put(th);
			th.start();
		} catch (InterruptedException ex) {
			throw new IllegalStateException("Could not create a new tasks thread.");
		}

		maybeSetTerminated();
	}

	private void maybeSetTerminated() {
		if (runState != RunState.RUNNING && threads.isEmpty()) {
			lock.lock();
			try {
				runState = RunState.TERMINATED;
				termination.signalAll();
			} finally {
				lock.unlock();
			}
		}
	}
}