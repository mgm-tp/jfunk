/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import com.google.common.base.Preconditions;

/**
 * Represents an e-mail account.
 * 
 * @version $Id$
 */
public class MailAccount {

	private final String accountId;
	private final String address;
	private final MailAuthenticator authenticator;

	public static final String MAIL_ACCOUNT_PREFIX = "mail.account.";

	/**
	 * Creates a new {@link MailAccount} instance.
	 * 
	 * @param accountId
	 *            The part of the e-mail address before the '@' symbol.
	 * @param user
	 *            The user
	 * @param password
	 *            The password
	 */
	public MailAccount(final String accountId, final String user, final String password, final String address) {
		Preconditions.checkNotNull(accountId, "AccountId must not be null.");

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

	public static class MailAuthenticator extends Authenticator {

		private final String user;
		private final String password;

		MailAuthenticator(final String user, final String password) {
			this.user = user;
			this.password = password;
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, password);
		}
	}
}