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

/**
 * Event posted on mail account reservation.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class MailAccountReservationEvent {

	private final String accountReservationKey;
	private final MailAccount mailAccount;

	MailAccountReservationEvent(final String accountReservationKey, final MailAccount mailAccount) {
		this.accountReservationKey = accountReservationKey;
		this.mailAccount = mailAccount;
	}

	/**
	 * @return the accountReservationKey
	 */
	public String getAccountReservationKey() {
		return accountReservationKey;
	}

	/**
	 * @return the mailAccount
	 */
	public MailAccount getMailAccount() {
		return mailAccount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accountReservationKey == null ? 0 : accountReservationKey.hashCode());
		result = prime * result + (mailAccount == null ? 0 : mailAccount.hashCode());
		return result;
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
		MailAccountReservationEvent other = (MailAccountReservationEvent) obj;
		if (accountReservationKey == null) {
			if (other.accountReservationKey != null) {
				return false;
			}
		} else if (!accountReservationKey.equals(other.accountReservationKey)) {
			return false;
		}
		if (mailAccount == null) {
			if (other.mailAccount != null) {
				return false;
			}
		} else if (!mailAccount.equals(other.mailAccount)) {
			return false;
		}
		return true;
	}
}
