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

import static com.google.common.base.Preconditions.checkArgument;

import javax.mail.Authenticator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Represents an e-mail account.
 * 
 */
public class MailAccount {

	private final String accountId;
	private String address;
	private final MailAuthenticator authenticator;
	private final Logger log = LoggerFactory.getLogger(getClass());

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
		authenticator = new MailAuthenticator(user, password);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MailAccount other = (MailAccount) obj;
		if (accountId == null) {
			if (other.accountId != null) {
				return false;
			}
		} else if (!accountId.equals(other.accountId)) {
			return false;
		}
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accountId == null ? 0 : accountId.hashCode());
		result = prime * result + (address == null ? 0 : address.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return accountId + ": " + address;
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

	/**
	 * Allows to set a new mail address. This is only possible when mail subaddressing is active and
	 * the new mail address belongs to the defined mail accountId.
	  *
	 * @param address
	 *            the new mail address
	 */
	public void setAddress(final String address) {
		// Do we have a subaddressing account?
		Preconditions.checkArgument(StringUtils.startsWith(this.address, accountId + "+"),
				"Mail address can only be changed when subaddressing is active");
		Preconditions.checkArgument(StringUtils.startsWith(address, accountId), "New mail address %s does not start with accountId=%s", address,
				accountId);
		log.info("Changing mail address from {} to {}", this.address, address);
		this.address = address;
	}
}
