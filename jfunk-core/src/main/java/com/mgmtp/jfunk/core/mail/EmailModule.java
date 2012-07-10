package com.mgmtp.jfunk.core.mail;

import com.google.inject.Provides;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;

/**
 * Guice module for e-mail handling. This module must be installed when e-mail support is necessary.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class EmailModule extends BaseJFunkGuiceModule {

	@Override
	protected void doConfigure() {
		bind(EmailParser.class);
		bind(EmailParserFactory.class);
		bind(MailHandler.class).to(DefaultMailHandler.class);
		bindEventHandler().to(EmailEventHandler.class);
		bindModuleScopedDisposable().to(DefaultMailHandler.class);
	}

	@Provides
	MailAccount provideMailAccount(final Configuration config, final MailHandler mailHandler) {
		return mailHandler.getMailAccount(config);
	}
}
