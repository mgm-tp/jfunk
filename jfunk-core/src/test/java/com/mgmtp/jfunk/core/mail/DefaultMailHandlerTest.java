/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.mail;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * Unit test for the DefaultMailHandler.
 * 
 */
public class DefaultMailHandlerTest {

	private static final Set<String> CONFIGURED_EMAILS = Collections.unmodifiableSet(Sets.newHashSet("test1", "test2", "test3"));
	private Set<String> accountIds;

	private MailHandler handler;

	@BeforeClass
	public void setUp() {
		handler = new DefaultMailHandler();
	}

	/**
	 * Creates properties for the test.
	 * 
	 * @return The Properties instance.
	 */
	public Configuration createProperties() {
		Configuration props = new Configuration(Charsets.UTF_8);
		props.put("mail.account.test1", "");
		props.put("mail.account.test2", "");
		props.put("mail.account.test3", "");
		return props;
	}

	/**
	 * Tests that a fixed e-mail accounts is used if set.
	 */
	@Test
	public void testGetFixedMailAccount() {
		Configuration props = createProperties();

		// Set fixed account
		props.put(EmailConstants.TESTING_EMAIL_ID, "foo");

		MailAccount account = handler.getMailAccount(props);

		// Account must not be one from the configured list.
		boolean foundConfiguredEMail = CONFIGURED_EMAILS.contains(account.getAccountId());
		Assert.assertEquals(foundConfiguredEMail, false);

		Assert.assertEquals(account.getAccountId(), "foo");

		// The same thread should get the same account again without blocking.
		MailAccount account2 = handler.getMailAccount(props);
		Assert.assertEquals(account, account2);
		handler.releaseAllMailAccountsForThread();
	}

	/**
	 * Tests retrieving a free e-mail accounts from the list of configured accounts.
	 */
	@Test
	public void testGetMailAccount() {
		Configuration props = createProperties();

		MailAccount account = handler.getMailAccount(props);

		// Account must be one from the configured list.
		boolean foundConfiguredEMail = CONFIGURED_EMAILS.contains(account.getAccountId());
		Assert.assertEquals(foundConfiguredEMail, true);

		// The same thread should get the same account again without blocking.
		MailAccount account2 = handler.getMailAccount(props);
		Assert.assertEquals(account, account2);
		handler.releaseAllMailAccountsForThread();
	}

	@BeforeGroups("parallel")
	public void setUpParallelGroup() {
		accountIds = new CopyOnWriteArraySet<String>();
	}

	/**
	 * <p style="font-style: italic">
	 * First test in a set of four parallel tests.
	 * </p>
	 * <p>
	 * Tests that all three configured accounts can be acquired by three different threads and that
	 * each thread gets a different account.
	 * </p>
	 */
	@Test(threadPoolSize = 3, invocationCount = 3, groups = "parallel")
	public void testGetMailAccountParallel1() {
		Configuration props = createProperties();

		MailAccount account = handler.getMailAccount(props);

		// Each thread must get a different account.
		Assert.assertEquals(accountIds.contains(account.getAccountId()), false);
		accountIds.add(account.getAccountId());
	}

	/**
	 * <p style="font-style: italic">
	 * Second test in a set of four parallel tests.
	 * </p>
	 * <p>
	 * The previous test has acquired all three available e-mail accounts, so another thread must
	 * not be able to acquire an e-mail account. This test verifies this behavior by explicitly
	 * interrupting the thread, which must trigger an InterruptedException and in turn a
	 * JFunkException if the thread is in a waiting state.
	 * </p>
	 */
	@Test(groups = "parallel", dependsOnMethods = "testGetMailAccountParallel1", expectedExceptions = JFunkException.class)
	public void testGetMailAccountParallel2() throws InterruptedException {
		class TestThread extends Thread {
			JFunkException threadEx = null;

			@Override
			public void run() {
				try {
					testGetMailAccountParallel1();
				} catch (JFunkException ex) {
					threadEx = ex;
				}
			}
		}

		TestThread th = new TestThread();
		th.start();
		// Trying to wait for the thread which must fail, because the thread is blocked,
		// so the alive check afterwards must be true.
		th.join(1000);
		Assert.assertEquals(th.isAlive(), true);

		// Interruption triggers a JFunkException. The thread is exited.
		th.interrupt();
		th.join();

		Assert.assertNotNull(th.threadEx, "Expected JFunkException was not thrown");

		throw th.threadEx;
	}

	/**
	 * <p style="font-style: italic">
	 * Third test in a set of four parallel tests.
	 * </p>
	 * <p>
	 * Releases all mail accounts and clears the id cache in preparation for the next test.
	 * </p>
	 */
	@Test(groups = "parallel", dependsOnMethods = "testGetMailAccountParallel2")
	public void testGetMailAccountParallel3() {
		handler.releaseAllMailAccounts();
		accountIds.clear();
	}

	/**
	 * <p style="font-style: italic">
	 * Last test in a set of four parallel tests.
	 * </p>
	 * <p>
	 * Executes the first test again which must pass because the e-mail accounts have been release
	 * by the previous test.
	 * </p>
	 */
	@Test(threadPoolSize = 3, invocationCount = 3, groups = "parallel", dependsOnMethods = "testGetMailAccountParallel3")
	public void testGetMailAccountParallel4() {
		testGetMailAccountParallel1();
	}

	/**
	 * Clean-up of the parallel group, so the other test may be executed.
	 */
	@AfterGroups("parallel")
	public void releaseMailAccounts() {
		handler.releaseAllMailAccounts();
	}
}