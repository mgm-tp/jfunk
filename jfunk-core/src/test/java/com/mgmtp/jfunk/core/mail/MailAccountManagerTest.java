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
package com.mgmtp.jfunk.core.mail;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newCopyOnWriteArrayList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import com.google.common.eventbus.EventBus;
import com.google.inject.util.Providers;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * Unit test for the DefaultMailHandler.
 *
 * @author rnaegele
 * @since 3.1.0
 */
public class MailAccountManagerTest {

	private static final int ACCOUNTS_PER_POOL = 3;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SetMultimap<String, MailAccount> emailAddressPools = HashMultimap.create();
	private final EventBus eventBus = new EventBus();

	@DataProvider(name = "poolSizes")
	public Object[][] createPoolSizes() {
		return new Object[][] {
			new Object[] { new Integer(1) },
			new Object[] { new Integer(2) }
		};
	}

	private MailAccountManager manager;
	private Configuration config;

	private void init(final int numPools) {
		for (int i = 0; i < numPools; ++i) {
			String pool = String.format("pool_%d", i);
			for (int j = 0; j < ACCOUNTS_PER_POOL; ++j) {
				String accountId = String.format("account.Id_%d_%d", i, j);
				String user = String.format("user_%d_%d", i, j);
				String password = String.format("password_%d_%d", i, j);
				String address = String.format("address_%d_%d", i, j);
				emailAddressPools.put(pool, new MailAccount(accountId, address, user, password));
			}
		}
		config = new Configuration(Charsets.UTF_8);
		manager = new MailAccountManager(emailAddressPools, new MathRandom(), Providers.of(eventBus), config);
	}

	@AfterMethod
	public void releaseMailAccounts() {
		manager.releaseAllMailAccounts();
	}

	@Test(dataProvider = "poolSizes")
	public void threadShouldAlwaysGetReservedAccoutAgain(final Integer numPools) throws InterruptedException {
		init(numPools);

		MailAccount account;
		MailAccount account2;
		Collection<MailAccount> pool;
		if (numPools > 1) {
			String poolName = "pool_1";
			account = manager.reserveMailAccount("key", poolName);
			pool = emailAddressPools.get(poolName);
			account2 = manager.reserveMailAccount("key", poolName);
		} else {
			account = manager.reserveMailAccount();
			pool = getOnlyElement(emailAddressPools.asMap().values());
			account2 = manager.reserveMailAccount();
		}

		boolean foundConfiguredEMail = pool.contains(account);
		assertThat(foundConfiguredEMail).describedAs("Configured account not found in pool: " + pool).isTrue();
		assertThat(account).describedAs("A different account was returned for the same thread").isSameAs(account2);

		final AtomicReference<MailAccount> accountRef = new AtomicReference<>();
		Thread th = new Thread() {
			@Override
			public void run() {
				MailAccount otherThreadAccount = manager.reserveMailAccount("another_thread_key", "pool_0");
				manager.releaseMailAccountForThread(otherThreadAccount);

				while (true) {
					// this is necessary to get better coverage and to check that this accounts is
					// not returned below for the other hread
					MailAccount otherThreadAccount2 = manager.reserveMailAccount("yet_another_thread_key", "pool_0");
					if (!otherThreadAccount.equals(otherThreadAccount2)) {
						accountRef.set(otherThreadAccount2);
						break;
					}
					manager.releaseMailAccountForThread(otherThreadAccount);
				}
			}
		};
		th.start();
		th.join();

		MailAccount otherThreadAcount = accountRef.get();
		MailAccount additionalAcount = manager.reserveMailAccount("additional_key", "pool_0");

		Set<MailAccount> accountsForThread = manager.getReservedMailAccountsForCurrentThread();
		assertThat(accountsForThread).describedAs("Account should not be reserved for thread").doesNotContain(otherThreadAcount);
		assertThat(accountsForThread).describedAs("Reserved accounts don't match expected").contains(account, additionalAcount);
	}

	@Test(dataProvider = "poolSizes")
	public void threadShouldNotGetAccountUsedByDifferentThread(final Integer numPools) throws InterruptedException {
		init(numPools);

		final List<MailAccount> accounts = newCopyOnWriteArrayList();
		List<Thread> threads = newArrayList();
		for (int i = 0; i < numPools; ++i) {
			final int poolIndex = i;
			for (int j = 0; j < ACCOUNTS_PER_POOL; ++j) {
				final int accountIndex = j;
				Thread th = new Thread() {
					@Override
					public void run() {
						MailAccount account = manager.reserveMailAccount(
							String.format("key_%d_%d", poolIndex, accountIndex),
							String.format("pool_%d", poolIndex));
						accounts.add(account);
					}
				};
				threads.add(th);
				th.start();
			}
		}

		for (Thread th : threads) {
			th.join();
		}

		Thread th = new Thread() {
			@Override
			public void run() {
				manager.reserveMailAccount("foo", "pool_0");
			}
		};
		th.start();
		th.join(500L);

		assertThat(th.isAlive()).describedAs("A reserved account was reused by a different thread.").isTrue();

		th = new Thread() {
			@Override
			public void run() {
				manager.releaseAllMailAccounts();
				manager.reserveMailAccount("key_0_0", "pool_0");
			}
		};
		th.start();
		th.join();

		assertThat(th.isAlive()).describedAs("A reserved account could not be released.").isFalse();
	}

	@Test
	public void releasedAccountsShouldBeAvailableAgain() throws InterruptedException {
		init(1);
		int j = 0;
		for (MailAccount mailAccount : emailAddressPools.get("pool_0")) {
			config.put("mail." + "key_" + j + ".accountId", mailAccount.getAccountId());
			config.put("mail." + "other_key_" + j++ + ".accountId", mailAccount.getAccountId());
		}

		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);

		Thread th = new Thread() {
			@Override
			public void run() {
				int i = 0;
				for (MailAccount mailAccount : emailAddressPools.get("pool_0")) {
					manager.reserveMailAccount("key_" + i++, mailAccount.getAccountId());
				}
				latch1.countDown();
				try {
					latch2.await();
				} catch (InterruptedException ex) {
					log.error(ex.getMessage(), ex);
				}
				manager.releaseAllMailAccountsForThread();
			}
		};
		th.start();
		latch1.await();

		th = new Thread() {
			@Override
			public void run() {
				int i = 0;
				for (MailAccount mailAccount : emailAddressPools.get("pool_0")) {
					manager.reserveMailAccount("other_key_" + i++, mailAccount.getAccountId());
				}
			}
		};
		th.start();
		th.join(500L);
		assertThat(th.isAlive()).describedAs("A reserved account was reused by a different thread.").isTrue();

		latch2.countDown();

		th.join();
		assertThat(th.isAlive()).describedAs("A reserved account could not be released.").isFalse();
	}

	@Test
	public void mailboxShouldBePurgedOnReservation() {
		init(1);
		MailboxPurger purger = mock(MailboxPurger.class);
		eventBus.register(purger);
		try {
			MailAccount mailAccount = manager.reserveMailAccount();
			verify(purger)
			.purgeMailbox(
				new MailAccountReservationEvent(MailAccountManager.DEFAULT_ACCOUNT_RESERVATION_KEY, mailAccount));
		} finally {
			eventBus.unregister(purger);
		}
	}

	@Test
	public void reservedAccountWithDefaultReservationKeyShouldBeAvailable() {
		init(1);
		MailAccount actual = manager.reserveMailAccount();
		MailAccount expected = Iterables.getOnlyElement(manager.getReservedMailAccountsForCurrentThread());
		assertThat(actual).isSameAs(expected);
	}
}
