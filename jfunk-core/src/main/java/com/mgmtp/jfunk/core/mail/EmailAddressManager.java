package com.mgmtp.jfunk.core.mail;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * @author rnaegele
 * @version $Id: $
 */
@Singleton
public class EmailAddressManager {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Provider<Configuration> configProvider;
	private final Map<String, List<MailAccount>> emailAddressPool;
	private final MathRandom random;
	private final String defaultPool;

	private final Map<MailAccount, ThreadReservationKeyWrapper> usedAccounts = newHashMap();
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	@Inject
	EmailAddressManager(final Map<String, List<MailAccount>> emailAddressPool, final Provider<Configuration> configProvider,
			final MathRandom random) {
		checkState(emailAddressPool.size() > 0, "E-mail address pool is empty.");
		this.emailAddressPool = emailAddressPool;
		this.configProvider = configProvider;
		this.random = random;

		this.defaultPool = emailAddressPool.size() == 1 ? getOnlyElement(emailAddressPool.keySet()) : null;
	}

	public MailAccount reserveMailAccount(final String reservationKey) {
		return reserveMailAccount(reservationKey, null);
	}

	public MailAccount reserveMailAccount(final String reservationKey, final String pool) {
		checkState(pool == null && emailAddressPool.size() == 1, "No pool specified but multiple pools available.");

		String poolKey = pool == null ? defaultPool : pool;
		List<MailAccount> addressPool = newArrayList(emailAddressPool.get(poolKey));
		Collections.shuffle(addressPool, random.getRandom());

		lock.lock();
		try {
			while (true) {
				MailAccount account = null;
				// Try to find a free account.
				for (MailAccount acc : addressPool) {
					ThreadReservationKeyWrapper wrapper = usedAccounts.get(acc);
					if (wrapper == null
							|| wrapper.thread == Thread.currentThread() && wrapper.reservationKey.equals(reservationKey)) {
						// If account is not yet used or already used by the current thread 
						// with the given reservation key, then use it
						account = acc;
						log.info("Using already reserved e-mail account: {}", account.getAccountId());
						break;
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

					usedAccounts.put(account, new ThreadReservationKeyWrapper(Thread.currentThread(), reservationKey));
					configProvider.get().put("mail.account." + reservationKey, account.getAccountId());
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

	public void releaseAllMailAccounts() {
		lock.lock();
		try {
			usedAccounts.clear();
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public void releaseAllMailAccountsForThread() {
		lock.lock();
		try {
			for (Iterator<Entry<MailAccount, ThreadReservationKeyWrapper>> it = usedAccounts.entrySet().iterator(); it.hasNext();) {
				if (it.next().getValue().thread == Thread.currentThread()) {
					it.remove();
				}
			}
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public void releaseMailAccount(final MailAccount account) {
		lock.lock();
		try {
			usedAccounts.remove(account);
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	static class ThreadReservationKeyWrapper {
		private final Thread thread;
		private final String reservationKey;

		ThreadReservationKeyWrapper(final Thread thread, final String reservationKey) {
			this.thread = checkNotNull(thread);
			this.reservationKey = checkNotNull(reservationKey);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + thread.hashCode();
			result = prime * result + reservationKey.hashCode();
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ThreadReservationKeyWrapper other = (ThreadReservationKeyWrapper) obj;
			if (!thread.equals(other.thread)) {
				return false;
			}
			if (!reservationKey.equals(other.reservationKey)) {
				return false;
			}
			return true;
		}
	}
}
