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
package com.mgmtp.jfunk.core.mail;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.filterValues;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.transformValues;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.eventbus.EventBus;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * <p>
 * {@link MailAccountManager} manages e-mail accounts used in functional tests. Multiple e-mail
 * address pool may be configured. Before an account can be used, it must be reserved using one of
 * the reservation methods of this class. Reservation always happens for the current thread.
 * </p>
 * <p>
 * <b>Configuration</b>
 * </p>
 * <p>
 * At least one address pool with at least one account must be configured as follows (without a
 * value for the key).
 * </p>
 * <p>
 * {@code mail.pool.<poolName>.<accountId>}
 * </p>
 * <p>
 * Pool name must not contain periods. E-mail address, user, and password are resolved using the
 * following configuration placeholders, i. e. it is possible to specify individual users, password,
 * and mail addresses per account with defaults as fallback.
 * </p>
 * 
 * <pre>
 * ${mail.pool.&lt;poolName&gt;.&lt;accountId&gt;.user,&lt;accountId&gt;}
 * ${mail.pool.&lt;poolName&gt;.&lt;accountId&gt;.password,${mail.default.password}}
 * ${mail.pool.&lt;poolName&gt;.&lt;accountId&gt;.address,&lt;accountId&gt;@${mail.default.domain}}
 * </pre>
 * 
 * The above keys can be interpreted as follows:
 * <ul>
 * <li>If no user is specified, the {@code accountId} is used.</li>
 * <li>If no password is specified, the default password configured by {@code mail.default.password}
 * is used.</li>
 * <li>If no address is specified, the address' local part is the {@code accountId}, the domain part
 * is that configured by {@code mail.default.domain}.</li>
 * </ul>
 * 
 * <p>
 * It is possible to configure fixed e-mail accounts. If an e-mail account is reserved, the
 * configuration is checked for the existance of a pre-configured fixed e-mail account under the key
 * {@code mail.<accountReservationKey>.accountId}. The configured {@code accountId} must match a
 * potentially already reserved one. If the pre-configured account is already used by another
 * thread, the reservation call blocks until the account is released.
 * </p>
 * <p>
 * A {@link MailAccountReservationEvent} is posted on the {@link EventBus} when an account is newly
 * reserved (and has not already been reserved) by a thread.
 * </p>
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@Singleton
public class MailAccountManager {
	public static final String DEFAULT_ACCOUNT_RESERVATION_KEY = "default_account_reservation_key";

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SetMultimap<String, MailAccount> emailAddressPools;
	private final MathRandom random;
	private final String defaultPool;

	private final Map<MailAccount, ThreadReservationKeyWrapper> usedAccounts = newHashMap();
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private final Provider<EventBus> eventBusProvider;
	private final Configuration config;

	@Inject
	MailAccountManager(final SetMultimap<String, MailAccount> emailAddressPools, final MathRandom random,
			final Provider<EventBus> eventBusProvider, final Configuration config) {
		checkState(emailAddressPools.keySet().size() > 0, "E-mail address pool is empty.");
		this.emailAddressPools = emailAddressPools;
		this.random = random;
		this.defaultPool = emailAddressPools.keySet().size() == 1 ? getOnlyElement(emailAddressPools.keySet()) : null;
		this.eventBusProvider = eventBusProvider;
		this.config = config;
	}

	private MailAccount lookupMailAccount(final String accountId) {
		for (MailAccount mailAccount : emailAddressPools.values()) {
			if (mailAccount.getAccountId().endsWith(accountId)) {
				return mailAccount;
			}
		}
		throw new IllegalStateException("No configured mail account found for accountId: " + accountId);
	}

	/**
	 * Reserves an available mail account from the single configured pool under
	 * {@link #DEFAULT_ACCOUNT_RESERVATION_KEY}. The method blocks until an account is available.
	 * 
	 * @return the reserved mail account
	 * @throws IllegalStateException
	 *             if more than one mail address pools are configured
	 */
	public MailAccount reserveMailAccount() {
		return reserveMailAccount(DEFAULT_ACCOUNT_RESERVATION_KEY);
	}

	/**
	 * Reserves an available mail account under the specified reservation key. The method blocks
	 * until an account is available.
	 * 
	 * @param accountReservationKey
	 *            the key under which to reserve the account
	 * @return the reserved mail account
	 * @throws IllegalStateException
	 *             if more than one mail address pools are configured
	 */
	public MailAccount reserveMailAccount(final String accountReservationKey) {
		return reserveMailAccount(accountReservationKey, null);
	}

	/**
	 * Reserves an available mail account from the specified pool under the specified reservation
	 * key. The method blocks until an account is available.
	 * 
	 * @param pool
	 *            the mail address pool to reserve an account from
	 * @param accountReservationKey
	 *            the key under which to reserve the account
	 * @return the reserved mail account
	 */
	public MailAccount reserveMailAccount(final String accountReservationKey, final String pool) {
		if (pool == null && emailAddressPools.keySet().size() > 1) {
			throw new IllegalStateException("No pool specified but multiple pools available.");
		}

		String poolKey = pool == null ? defaultPool : pool;
		List<MailAccount> addressPool = newArrayList(emailAddressPools.get(poolKey));
		Collections.shuffle(addressPool, random.getRandom());

		return reserveAvailableMailAccount(accountReservationKey, addressPool);
	}

	private MailAccount reserveAvailableMailAccount(final String accountReservationKey, final List<MailAccount> addressPool) {
		checkNotNull(accountReservationKey, "'accountReservationKey' must not be null");
		checkNotNull(addressPool, "'addressPool' must not be null");

		lock.lock();
		try {
			while (true) {
				String fixedAccountId = config.get("mail." + accountReservationKey + ".accountId");

				MailAccount account = lookupUsedMailAccountForCurrentThread(accountReservationKey);
				if (!isNullOrEmpty(fixedAccountId)) {
					if (account != null) {
						checkState(
								account.getAccountId().equals(fixedAccountId),
								"Fixed configured mail account does not match that already reserved (configured=%s, reserved=%s)",
								fixedAccountId, account.getAccountId());
					} else {
						account = lookupMailAccount(fixedAccountId);
						if (isReserved(account)) {
							// Already reserved by another thread, so we cannot use it.
							// Set it to null in order to trigger the wait below.
							account = null;
						}
					}
				} else {
					checkState(!addressPool.isEmpty(), "No fixed e-mail account configured and specified pool is empty.");
					if (account != null) {
						checkState(addressPool.contains(account),
								"Account '%s' is already reserved under key: %s", account, accountReservationKey);
						log.info("Using already reserved e-mail account: {}", account.getAccountId());
						return account;
					}

					// Try to find a free account.
					for (MailAccount acc : addressPool) {
						ThreadReservationKeyWrapper wrapper = usedAccounts.get(acc);
						if (wrapper == null) {
							account = acc;
							break;
						}
					}
				}

				if (account == null) {
					// No free account available. We wait and then start over with the loop.
					log.info("No free e-mail account available. Waiting...");
					condition.await();
				} else {
					// We've found a free account and return it.
					String accountId = account.getAccountId();
					log.info("Found free e-mail account: {}", accountId);

					usedAccounts.put(account, new ThreadReservationKeyWrapper(Thread.currentThread(), accountReservationKey));

					// post account reservation
					eventBusProvider.get().post(new MailAccountReservationEvent(accountReservationKey, account));

					return account;
				}
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new JFunkException(ex.getMessage(), ex);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Looks up the mail account that is reserved for the current thread under the specified key.
	 * 
	 * @param accountReservationKey
	 *            the reservation key
	 * @return the reserved account, or {@code null} if none is found
	 */
	public MailAccount lookupUsedMailAccountForCurrentThread(final String accountReservationKey) {
		lock.lock();
		try {
			Map<MailAccount, ThreadReservationKeyWrapper> usedAccountsForThread = filterValues(usedAccounts,
					new CurrentThreadWrapperPredicate());

			if (!usedAccountsForThread.isEmpty()) {
				for (Entry<MailAccount, ThreadReservationKeyWrapper> entry : usedAccountsForThread.entrySet()) {
					if (entry.getValue().accountReservationKey.equals(accountReservationKey)) {
						return entry.getKey();
					}
				}
			}

			return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param mailAccount
	 *            the mail account
	 * @return whether the specified mail account has been reserved by some thread
	 */
	public boolean isReserved(final MailAccount mailAccount) {
		return usedAccounts.containsKey(mailAccount);
	}

	/**
	 * Gets the set of reserved mail accounts for the current thread.
	 * 
	 * @return the set of reserved mail accounts
	 */
	public Set<MailAccount> getReservedMailAccountsForCurrentThread() {
		lock.lock();
		try {
			Map<MailAccount, ThreadReservationKeyWrapper> accountsForThread = filterValues(usedAccounts,
					new CurrentThreadWrapperPredicate());
			return ImmutableSet.copyOf(accountsForThread.keySet());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the set of reservation key under which accounts are reserved for the current thread.
	 * 
	 * @return the set of reservation keys
	 */
	public Set<String> getRegisteredAccountReservationKeysForCurrentThread() {
		lock.lock();
		try {
			Map<MailAccount, String> accountsForThread = transformValues(filterValues(usedAccounts,
					new CurrentThreadWrapperPredicate()),
					new Function<ThreadReservationKeyWrapper, String>() {
						@Override
						public String apply(final ThreadReservationKeyWrapper input) {
							return input.accountReservationKey;
						}
					}
					);

			return ImmutableSet.copyOf(accountsForThread.values());
		} finally {
			lock.unlock();
		}
	}

	void releaseAllMailAccounts() {
		log.info("Releasing all mail accounts...");
		lock.lock();
		try {
			usedAccounts.clear();
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Releases all reserved mail accounts for the current thread so they can be reused by other
	 * threads. Threads blocking on an attempt to reserved an account are notified.
	 */
	public void releaseAllMailAccountsForThread() {
		log.info("Releasing all mail accounts for the current thread...");
		lock.lock();
		try {
			for (Iterator<Entry<MailAccount, ThreadReservationKeyWrapper>> it = usedAccounts.entrySet().iterator(); it.hasNext();) {
				ThreadReservationKeyWrapper wrapper = it.next().getValue();
				if (wrapper.thread == Thread.currentThread()) {
					it.remove();
				}
			}
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Releases the specified mail account for the current thread so it can be reused by another
	 * threads. Threads blocking on an attempt to reserved an account are notified.
	 * 
	 * @param account
	 *            the account to release
	 */
	public void releaseMailAccountForThread(final MailAccount account) {
		log.debug("Releasing mail account for the current thread: %s", account);
		lock.lock();
		try {
			ThreadReservationKeyWrapper wrapper = usedAccounts.get(account);
			if (wrapper != null) {
				if (wrapper.thread == Thread.currentThread()) {
					log.debug("Releasing mail account: {}", account);
					usedAccounts.remove(account);
					condition.signalAll();
				} else {
					log.warn("Cannot release a mail account reserved by a different thread: {}", account);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Releases the mail account reserved under the specified reservation key for the current thread
	 * so it can be reused by another threads. Threads blocking on an attempt to reserved an account
	 * are notified.
	 * 
	 * @param accountReservationKey
	 *            the reservation key
	 */
	public void releaseMailAccountForThread(final String accountReservationKey) {
		lock.lock();
		try {
			MailAccount mailAccount = lookupUsedMailAccountForCurrentThread(accountReservationKey);
			releaseMailAccountForThread(mailAccount);
		} finally {
			lock.unlock();
		}
	}

	static class CurrentThreadWrapperPredicate implements Predicate<ThreadReservationKeyWrapper> {
		@Override
		public boolean apply(final ThreadReservationKeyWrapper input) {
			return input.thread == Thread.currentThread();
		}
	}

	static class ThreadReservationKeyWrapper {
		private final Thread thread;
		private final String accountReservationKey;

		ThreadReservationKeyWrapper(final Thread thread, final String accountReservationKey) {
			this.thread = checkNotNull(thread);
			this.accountReservationKey = checkNotNull(accountReservationKey);
		}
	}
}
