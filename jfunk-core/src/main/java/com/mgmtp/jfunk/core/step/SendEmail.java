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
package com.mgmtp.jfunk.core.step;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.mgmtp.jfunk.core.config.InjectConfig;
import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.mail.EmailConstants;
import com.mgmtp.jfunk.core.mail.EmailParser;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * Sends an e-mail.
 * 
 */
public class SendEmail extends BaseStep {

	private final String subject;
	private final String body;
	private final String recipients;

	@Inject
	EmailParser emailParser;

	@InjectConfig(name = EmailConstants.MAIL_ADDRESS)
	String emailAddress;

	/**
	 * Creates a new instance.
	 * 
	 * @param subject
	 *            the subject
	 * @param body
	 *            the message body
	 * @param recipients
	 *            a comma-separated list of recipients (addresses must follow RFC822 syntax)
	 */
	public SendEmail(final String subject, final String body, final String recipients) {
		this.subject = subject;
		this.body = body;
		this.recipients = recipients;
	}

	@Override
	public void execute() {
		log.info("Sending e-mail to: {}", recipients);

		MimeMessage msg = new MimeMessage(emailParser.getSession());
		try {
			msg.setSubject(subject);
			msg.setText(body);
			msg.setFrom(new InternetAddress(emailAddress));
			msg.setRecipients(Message.RecipientType.TO, recipients);
			emailParser.send(msg);
		} catch (Exception e) {
			throw new StepException("Error sending e-mail to: " + recipients, e);
		}
	}
}