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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mgmtp.jfunk.core.event.AfterScriptEvent;

/**
 * Event handler that auto-releases all reserved mail accounts for the current thread when the
 * script is done.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@Singleton
public class MailAccountReleaser {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Provider<MailAccountManager> mailAccountManagerProvider;

	@Inject
	MailAccountReleaser(final Provider<MailAccountManager> mailAccountManagerProvider) {
		this.mailAccountManagerProvider = mailAccountManagerProvider;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void releaseMailAccountsForThread(@SuppressWarnings("unused") final AfterScriptEvent event) {
		log.info("Auto-releasing mail accounts...");
		mailAccountManagerProvider.get().releaseAllMailAccountsForThread();
	}
}
