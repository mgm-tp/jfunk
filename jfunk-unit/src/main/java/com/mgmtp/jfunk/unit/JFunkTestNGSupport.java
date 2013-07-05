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
package com.mgmtp.jfunk.unit;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;

/**
 * Provides integration of jFunk into TestNG. Must be used with the {@link Listeners} annotation. An
 * instance of {@link JFunkRunner} can then be injected into the test class.
 * 
 * <pre>
 * 
 * &#064;Listeners(JFunkTestNGSupport.class)
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
public final class JFunkTestNGSupport extends UnitSupport implements IInvokedMethodListener, ITestListener {

	@Override
	public void onStart(final ITestContext context) {
		Object testClassInstance = context.getAllTestMethods()[0].getInstance();
		init(testClassInstance);
		beforeTest(testClassInstance);
	}

	@Override
	public void beforeInvocation(final IInvokedMethod method, final ITestResult testResult) {
		if (method.isTestMethod()) {
			beforeScript(method.getTestMethod().getMethodName());
		}
	}

	@Override
	public void afterInvocation(final IInvokedMethod method, final ITestResult testResult) {
		if (method.isTestMethod()) {
			afterScript(method.getTestMethod().getMethodName(), testResult.isSuccess(), testResult.getThrowable());
		}
	}

	@Override
	public void onFinish(final ITestContext context) {
		afterTest();
	}

	@Override
	public void onTestStart(final ITestResult result) {
		// no-op
	}

	@Override
	public void onTestSuccess(final ITestResult result) {
		// no-op
	}

	@Override
	public void onTestFailure(final ITestResult result) {
		// no-op
	}

	@Override
	public void onTestSkipped(final ITestResult result) {
		// no-op
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
		// no-op
	}
}
