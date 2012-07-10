package com.mgmtp.jfunk.core.mail;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.event.BeforeModuleEvent;
import com.mgmtp.jfunk.core.exception.MailException;

/**
 * Event handler for deleting e-mails before a module is executed.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Singleton
public class EmailEventHandler {
	private final Logger log = Logger.getLogger(getClass());

	private final Provider<EmailParser> emailParserProvider;
	private final Provider<Configuration> configProvider;

	@Inject
	public EmailEventHandler(final Provider<EmailParser> emailParserProvider, final Provider<Configuration> configProvider) {
		this.emailParserProvider = emailParserProvider;
		this.configProvider = configProvider;
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
}
