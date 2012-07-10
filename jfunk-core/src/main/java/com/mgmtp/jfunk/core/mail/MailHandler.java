package com.mgmtp.jfunk.core.mail;

import com.mgmtp.jfunk.common.util.Configuration;

/**
 * Interface for handling e-mail accounts.
 * 
 * @version $Id$
 */
public interface MailHandler {

	/**
	 * Retrieves a free mail account, blocking if currently none is available.
	 * 
	 * @return The mail account.
	 */
	MailAccount getMailAccount(final Configuration config);

	/**
	 * Releases the specified mail account, so it may be used by another thread.
	 * 
	 * @param accountId
	 *            The id of the mail account.
	 */
	void releaseMailAccount(final String accountId);

	/**
	 * Releases all mail accounts that were used by the current thread.
	 */
	void releaseAllMailAccountsForThread();

	/**
	 * Releases all mail accounts.
	 */
	void releaseAllMailAccounts();
}