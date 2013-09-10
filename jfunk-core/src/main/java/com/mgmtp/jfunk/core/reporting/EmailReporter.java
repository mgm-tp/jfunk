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
package com.mgmtp.jfunk.core.reporting;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.apache.commons.io.IOUtils.LINE_SEPARATOR;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.mgmtp.jfunk.core.exception.MailException;
import com.mgmtp.jfunk.core.mail.SmtpClient;
import com.mgmtp.jfunk.core.mail.TransportSession;

/**
 * <p>
 * Generates a report email in HTML format.
 * </p>
 * <p>
 */
@ThreadSafe
public final class EmailReporter implements Reporter {
	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
	private static final FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss.SSS");
	private static final FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	private final Logger log = Logger.getLogger(getClass());

	private final List<ReportData> reportDataList = newArrayList();
	private final Provider<String> recipientsProvider;
	private final Provider<SmtpClient> smtpClientProvider;
	private final Provider<Session> sessionProvider;

	@Inject
	public EmailReporter(final Provider<SmtpClient> smtpClientProvider,
			@Nullable @ReportMailRecipients final Provider<String> recipientsProvider,
			@TransportSession final Provider<Session> sessionProvider) {
		this.smtpClientProvider = smtpClientProvider;
		this.recipientsProvider = recipientsProvider;
		this.sessionProvider = sessionProvider;
	}

	@Override
	public String getName() {
		return "E-mail Report";
	}

	@Override
	public synchronized void addResult(final ReportData reportData) {
		reportDataList.add(reportData);
	}

	@Override
	public synchronized void createReport() throws IOException {
		if (trimToNull(recipientsProvider.get()) == null) {
			log.warn("No recipients for global e-mail report set. Cannot send report.");
			return;
		}

		if (!reportDataList.isEmpty()) {
			String content = createEmailContent();
			sendMessage(content);
			reportDataList.clear();
		}
	}

	private void sendMessage(final String content) {
		try {
			MimeMessage msg = new MimeMessage(sessionProvider.get());
			msg.setSubject("jFunk E-mail Report");
			msg.addRecipients(Message.RecipientType.TO, recipientsProvider.get());

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html; charset=UTF-8");

			MimeMultipart multipart = new MimeMultipart("related");
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(getClass().getResource("check.gif")));
			messageBodyPart.setHeader("Content-ID", "<check>");
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(getClass().getResource("error.gif")));
			messageBodyPart.setHeader("Content-ID", "<error>");
			multipart.addBodyPart(messageBodyPart);

			msg.setContent(multipart);

			smtpClientProvider.get().send(msg);

			int anzahlRecipients = msg.getAllRecipients().length;
			log.info("Report e-mail was sent to " + anzahlRecipients + " recipient(s): " + recipientsProvider.get());
		} catch (MessagingException e) {
			log.error("Error while creating report e-mail", e);
		} catch (MailException e) {
			log.error("Error while sending report e-mail", e);
		}
	}

	private String createEmailContent() throws IOException {
		int size = reportDataList.size();
		List<String> rowData = newArrayListWithCapacity(size);

		String reportRowTemplate = Resources.toString(getClass().getResource("email-report-row-template.html"),
				Charset.forName("UTF-8"));
		for (int i = 0; i < size; ++i) {
			ReportData data = reportDataList.get(i);

			String rowContent = replacePlaceholderToken(reportRowTemplate, "counter", String.valueOf(i));
			rowContent = replacePlaceholderToken(rowContent, "date", DATE_FORMAT.format(data.getStartMillis()));
			rowContent = replacePlaceholderToken(rowContent, "start", TIME_FORMAT.format(data.getStartMillis()));
			rowContent = replacePlaceholderToken(rowContent, "finish", TIME_FORMAT.format(data.getStopMillis()));
			rowContent = replacePlaceholderToken(rowContent, "duration",
					DurationFormatUtils.formatDurationHMS(data.getStopMillis() - data.getStartMillis()));
			rowContent = replacePlaceholderToken(rowContent, "testobject", data.getTestObjectName());

			Throwable th = data.getThrowable();
			if (th == null) {
				rowContent = replacePlaceholderToken(rowContent, "image", "check");
				rowContent = replacePlaceholderToken(rowContent, "errormsg", "");
				rowContent = replacePlaceholderToken(rowContent, "style", "success");
			} else {
				rowContent = replacePlaceholderToken(rowContent, "image", "error");

				String msg = th.getMessage();

				Throwable root = th;
				while (root.getCause() != null) {
					root = root.getCause();
				}

				String rootMsg = root.getMessage();
				if (rootMsg != null && !rootMsg.equals(msg)) {
					msg += " - Root Message: " + rootMsg;
				}

				if (isBlank(msg)) {
					msg = th.getClass().getName();
				}

				rowContent = replacePlaceholderToken(rowContent, "errormsg", msg);
				rowContent = replacePlaceholderToken(rowContent, "style", "error");
			}

			rowData.add(rowContent);
		}

		String reportTemplate = Resources
				.toString(getClass().getResource("email-report-template.html"), Charset.forName("UTF-8"));
		reportTemplate = replacePlaceholderToken(reportTemplate, "timestamp", TIMESTAMP_FORMAT.format(new Date()), false);

		String reportData = on(LINE_SEPARATOR).join(rowData);
		reportTemplate = replacePlaceholderToken(reportTemplate, "rows", reportData, false);
		return reportTemplate;
	}

	private String replacePlaceholderToken(final String template, final String token, final String value, final boolean escapeHtml) {
		return template.replace("${" + token + "}", escapeHtml ? StringEscapeUtils.escapeHtml4(value) : value);
	}

	private String replacePlaceholderToken(final String template, final String token, final String value) {
		return replacePlaceholderToken(template, token, Strings.nullToEmpty(value), true);
	}
}