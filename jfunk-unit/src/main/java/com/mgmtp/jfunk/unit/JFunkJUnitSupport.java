package com.mgmtp.jfunk.unit;

import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * {@link Runner} implementation for integrating jFunk into JUnit. Must be used with the
 * {@link RunWith} annotation. An instance of {@link JFunkRunner} can then be injected into the test
 * class. Requires JUnit 4.7 or higher.
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
 * @version $Id$
 */
public final class JFunkJUnitSupport extends BlockJUnit4ClassRunner {

	private volatile UnitSupport unitSupport;

	public JFunkJUnitSupport(final Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	public void run(final RunNotifier notifier) {
		notifier.addListener(new JUnitListener());
		super.run(notifier);

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

		// We initialize jFunk only once per test class.
		if (unitSupport == null) {
			synchronized (this) {
				if (unitSupport == null) {
					unitSupport = new UnitSupport();
					unitSupport.init(testClassInstance);
				}
			}
		}

		// As JUnit creates a new instance for every test case (i. e. method)
		// we need to perform DI on the test class instance here.
		unitSupport.beforeTest(testClassInstance);
		return testClassInstance;
	}

	final class JUnitWatchman extends TestWatchman {

		@Override
		public void starting(final FrameworkMethod method) {
			unitSupport.beforeScript(method.getName());
		}

		@Override
		public void succeeded(final FrameworkMethod method) {
			unitSupport.afterScript(true, null);
		}

		@Override
		public void failed(final Throwable th, final FrameworkMethod method) {
			unitSupport.afterScript(false, th);
		}
	}

	final class JUnitListener extends RunListener {
		@Override
		public void testRunFinished(final Result result) throws Exception {
			unitSupport.afterTest();
		}
	}
}
