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

import static com.google.common.base.Preconditions.checkArgument;

import javax.mail.Authenticator;

/**
 * Represents an e-mail account.
 * 
 */
public class MailAccount {

	private final String accountId;
	private final String address;
	private final MailAuthenticator authenticator;

	/**
	 * Creates a new {@link MailAccount} instance.
	 * 
	 * @param accountId
	 *            The part of the e-mail address before the '@' symbol.
	 * @param user
	 *            The user
	 * @param password
	 *            The password
	 * @param address
	 *            the e-mail address
	 */
	public MailAccount(final String accountId, final String address, final String user, final String password) {
		checkArgument(accountId != null, "'accountId' must not be null.");
		checkArgument(address != null, "'address' must not be null.");

		this.accountId = accountId;
		this.address = address;
		this.authenticator = new MailAuthenticator(user, password);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final MailAccount that = (MailAccount) o;
		return accountId.equals(that.accountId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		return prime * result + accountId.hashCode();
	}

	@Override
	public String toString() {
		return accountId;
	}

	/**
	 * Creates and returns an {@link Authenticator} instances encapsulating the credentials of this
	 * mail account.
	 * 
	 * @return the authentificator
	 */
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	/**
	 * Returns the accountId.
	 * 
	 * @return The accountId
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
}
