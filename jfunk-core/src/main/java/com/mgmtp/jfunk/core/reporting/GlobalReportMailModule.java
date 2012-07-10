package com.mgmtp.jfunk.core.reporting;

import com.google.inject.Provides;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;
import com.mgmtp.jfunk.core.mail.EmailConstants;

/**
 * @author rnaegele
 * @version $Id$
 */
public class GlobalReportMailModule extends BaseJFunkGuiceModule {

	@Override
	protected void doConfigure() {
		bindGlobalReporter().to(EmailReporter.class);
	}

	@Provides
	@ReportMailRecipients
	String provideGlobalReportMailRecipients(final Configuration config) {
		return config.get(EmailConstants.REPORT_MAIL_RECIPIENTS);
	}
}
