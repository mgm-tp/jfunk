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

import org.apache.log4j.Logger;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.event.AfterScriptEvent;
import com.mgmtp.jfunk.core.event.BeforeModuleEvent;
import com.mgmtp.jfunk.core.exception.MailException;

/**
 * Event handler for deleting e-mails before a module is executed.
 * 
 * @author rnaegele
 */
@Singleton
public class EmailEventHandler {
	private final Logger log = Logger.getLogger(getClass());

	private final Provider<EmailParser> emailParserProvider;
	private final Provider<Configuration> configProvider;
	private final MailHandler mailHandler;

	@Inject
	public EmailEventHandler(final Provider<EmailParser> emailParserProvider, final Provider<Configuration> configProvider,
			final MailHandler mailHandler) {
		this.emailParserProvider = emailParserProvider;
		this.configProvider = configProvider;
		this.mailHandler = mailHandler;
	}

	/**
	 * Deletes e-mails if the configuration property {@link EmailConstants#MAIL_STARTUP_DELETE_ALL}
	 * is set to {@code true}.
	 */
	@Subscribe
	@AllowConcurrentEvents
	public void handleEvent(@SuppressWarnings("unused") final BeforeModuleEvent event) {
		if (configProvider.get().getBoolean(EmailConstants.MAIL_STARTUP_DELETE_ALL, false)) {
			try {
				emailParserProvider.get().deleteAllEmails();
			} catch (MailException ex) {
				log.error("Error deleting mails on startup.", ex);
			}
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleEvent(@SuppressWarnings("unused") final AfterScriptEvent event) {
		mailHandler.releaseAllMailAccountsForThread();
	}
}
