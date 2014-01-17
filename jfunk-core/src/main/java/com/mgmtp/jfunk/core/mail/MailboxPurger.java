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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.collect.Table;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mgmtp.jfunk.core.mail.StoreManager.FileMessageWrapper;

/**
 * Purges a mail account on account reservation. Subscribes to {@link MailAccountReservationEvent}s.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@Singleton
public class MailboxPurger {

	private final Provider<MailService> emailServiceProvider;
	private final Provider<Boolean> deleteOnStartupProvider;
	private final Provider<Table<String, String, FileMessageWrapper>> mailAccountCacheProvider;

	@Inject
	MailboxPurger(final Provider<MailService> emailServiceProvider,
			@MailDeleteOnReservation final Provider<Boolean> deleteOnStartupProvider,
			final Provider<Table<String, String, FileMessageWrapper>> mailAccountCacheProvider) {
		this.emailServiceProvider = emailServiceProvider;
		this.deleteOnStartupProvider = deleteOnStartupProvider;
		this.mailAccountCacheProvider = mailAccountCacheProvider;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void purgeMailbox(final MailAccountReservationEvent event) {
		if (deleteOnStartupProvider.get()) {
			emailServiceProvider.get().deleteMessages(event.getAccountReservationKey());
		}
		mailAccountCacheProvider.get().row(event.getMailAccount().getAccountId()).clear();
	}
}
