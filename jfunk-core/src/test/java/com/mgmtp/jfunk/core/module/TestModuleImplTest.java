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
package com.mgmtp.jfunk.core.module;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

import org.apache.commons.lang3.mutable.MutableInt;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.eventbus.EventBus;
import com.google.inject.Injector;
import com.mgmtp.jfunk.core.scripting.ExecutionMode;
import com.mgmtp.jfunk.core.scripting.StepExecutor;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * @author rnaegele
 */
public class TestModuleImplTest {

	private TestModuleImpl module;
	private MutableInt counter;

	@BeforeMethod
	public void setUp() {
		counter = new MutableInt();
		module = new CountingTestModul(counter);
		module.stepExecutor = new StepExecutor(mock(Injector.class), mock(EventBus.class));
	}

	@Test
	public void testExecuteAll() {
		module.stepExecutor = new StepExecutor(mock(Injector.class), mock(EventBus.class));
		module.executionMode = ExecutionMode.all;
		module.execute();
		assertEquals(counter.intValue(), 7);
	}

	@Test
	public void testExecuteStartToFirstBreak() {
		module.stepExecutor = new StepExecutor(mock(Injector.class), mock(EventBus.class));
		module.executionMode = ExecutionMode.start;
		module.breakIndex = 0;
		module.execute();
		assertEquals(counter.intValue(), 3);
	}

	@Test
	public void testExecuteStartToSecondBreak() {
		module.stepExecutor = new StepExecutor(mock(Injector.class), mock(EventBus.class));
		module.executionMode = ExecutionMode.start;
		module.breakIndex = 1;
		module.execute();
		assertEquals(counter.intValue(), 5);
	}

	@Test
	public void testExecuteFinishFromFirstBreak() {
		module.stepExecutor = new StepExecutor(mock(Injector.class), mock(EventBus.class));
		module.executionMode = ExecutionMode.finish;
		module.breakIndex = 0;
		module.execute();
		assertEquals(counter.intValue(), 4);
	}

	@Test
	public void testExecuteFinishFromSecondBreak() {
		module.stepExecutor = new StepExecutor(mock(Injector.class), mock(EventBus.class));
		module.executionMode = ExecutionMode.finish;
		module.breakIndex = 1;
		module.execute();
		assertEquals(counter.intValue(), 2);
	}

	static class CountingTestModul extends TestModuleImpl {

		private final MutableInt counter;

		public CountingTestModul(final MutableInt counter) {
			super("count", "count");
			this.counter = counter;
		}

		@Override
		protected void executeSteps() {
			executeSteps(
					new IncrementerStep(),
					new IncrementerStep());
			executeStep(new IncrementerStep(), true);
			executeStep(new IncrementerStep());
			executeStep(new IncrementerStep(), true);
			executeSteps(
					new IncrementerStep(),
					new IncrementerStep());
		}

		class IncrementerStep extends BaseStep {
			@Override
			public void execute() {
				counter.increment();
			}
		}
	}
}
