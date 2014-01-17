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
package com.mgmtp.jfunk.unit;

import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * {@link Runner} implementation for integrating jFunk into JUnit. Must be used with the {@link RunWith} annotation. An instance
 * of {@link JFunkRunner} can then be injected into the test class. Requires JUnit 4.7 or higher.
 * 
 * <pre>
 * 
 * &#064;RunWith(JFunkJUnitSupport.class)
 * public class MyTestCase {
 * 
 * 	&#064;Inject
 * 	JFunkRunner jFunkRunner;
 * 
 * 	&#064;Test
 * 	public void runMyModules() {
 * 		jFunkRunner.run(new FooModule());
 * 		jFunkRunner.run(new BarModule());
 * 	}
 * }
 * </pre>
 * 
 * @author rnaegele
 */
public final class JFunkJUnitSupport extends BlockJUnit4ClassRunner {

	public JFunkJUnitSupport(final Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List<MethodRule> rules(final Object test) {
		List<MethodRule> rules = super.rules(test);
		rules.add(new MethodRule() {
			@Override
			public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
				return new JUnitWatchman().apply(base, method, target);
			}
		});
		return rules;
	}

	@Override
	protected Object createTest() throws Exception {
		Object testClassInstance = super.createTest();

		// As JUnit creates a new instance for every test case (i. e. method)
		// we need to perform DI on the test class instance here.
		UnitSupport.getInstance().beforeTest(testClassInstance);
		return testClassInstance;
	}

	final class JUnitWatchman extends TestWatchman {

		@Override
		public void starting(final FrameworkMethod method) {
			UnitSupport.getInstance().beforeScript(method.getName());
		}

		@Override
		public void succeeded(final FrameworkMethod method) {
			UnitSupport.getInstance().afterScript(method.getName(), true, null);
		}

		@Override
		public void failed(final Throwable th, final FrameworkMethod method) {
			UnitSupport.getInstance().afterScript(method.getName(), false, th);
		}
	}
}
