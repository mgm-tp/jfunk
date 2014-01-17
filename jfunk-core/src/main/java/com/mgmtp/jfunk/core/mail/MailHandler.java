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
package com.mgmtp.jfunk.core.mail;

import com.mgmtp.jfunk.common.util.Configuration;

/**
 * Interface for handling e-mail accounts.
 * 
 */
@Deprecated
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
