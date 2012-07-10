package com.mgmtp.jfunk.core.mail;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.mgmtp.jfunk.common.util.Configuration;

/**
 * Factory for creating additional unscoped e-mail parsers.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Singleton
public class EmailParserFactory {

	private final Provider<MailAccount> mailAccoutProvider;
	private final Configuration config;

	@Inject
	public EmailParserFactory(final Configuration config, final Provider<MailAccount> mailAccoutProvider) {
		this.config = config;
		this.mailAccoutProvider = mailAccoutProvider;
	}

	public EmailParser createEmailParser() {
		return new EmailParser(config, mailAccoutProvider.get());
	}
}
