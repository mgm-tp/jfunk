package com.mgmtp.jfunk.core.step;

import java.io.File;
import java.util.regex.Matcher;

import javax.inject.Inject;
import javax.mail.MessagingException;

import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.ModuleArchiveDir;
import com.mgmtp.jfunk.core.exception.MailException;
import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.mail.BaseMailObject;
import com.mgmtp.jfunk.core.mail.EmailParser;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * <p>
 * Searches for an e-mail based on regular expression for subject and body. If no matching e-mail is
 * found before a timeout occurs, a {@link StepException} is thrown.
 * </p>
 * <p>
 * The optional {@code groupConfigKeys} parameters are used as keys for storing values from caturing
 * groups in the body regex. The value of the first capturing group is stored under the first
 * {@code groupConfigKeys} value, the second group under the second {@code groupConfigKeys} value,
 * and so on.
 * </p>
 * 
 * @version $Id$
 */
public class EmailStep extends BaseStep {

	private final String subjectExpression;
	private final String bodyExpression;
	private final String[] groupConfigKeys;

	@Inject
	EmailParser emailParser;

	@Inject
	@ModuleArchiveDir
	File moduleArchiveDir;

	@Inject
	Configuration config;

	@Deprecated
	public EmailStep(final String subjectExpression, final String bodyExpression, @SuppressWarnings("unused") final TestModule test,
			final String... groupConfigKeys) {
		this(subjectExpression, bodyExpression, groupConfigKeys);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param subjectExpression
	 *            a regular expression for matching the e-mail subject
	 * @param bodyExpression
	 *            a regular expression for matching the e-mail body
	 * @param groupConfigKeys
	 *            if specified, values of capturing groups are extracted and saved in the
	 *            {@linkplain Configuration configuration} under these keys in sequential order
	 *            starting with group one.
	 */
	public EmailStep(final String subjectExpression, final String bodyExpression, final String... groupConfigKeys) {
		this.subjectExpression = subjectExpression;
		this.bodyExpression = bodyExpression;
		this.groupConfigKeys = groupConfigKeys;
	}

	/**
	 * Searches for an the e-mail with the given subject and body patterns, optionally extracting
	 * values of capturing groups, if specified.
	 * 
	 * @throws StepException
	 *             if no matching e-mail could be found
	 */
	@Override
	public void execute() throws StepException {
		BaseMailObject mail = new BaseMailObject(subjectExpression, bodyExpression, emailParser, moduleArchiveDir) {
			@Override
			protected void processBody(final Matcher mB) {
				if (groupConfigKeys != null && groupConfigKeys.length > 0) {
					for (int i = 0; i < mB.groupCount() && i < groupConfigKeys.length; i++) {
						String key = groupConfigKeys[i];
						String value = mB.group(i + 1);
						log.info("Setting property {} to {}", key, value);
						config.put(key, value);
					}
				}
			}
		};
		boolean success = false;
		try {
			success = mail.read();
		} catch (MailException e) {
			throw new StepException("Error searching for e-mail message: " + mail, e);
		} catch (MessagingException e) {
			throw new StepException("Error searching for e-mail message: " + mail, e);
		}
		if (!success) {
			throw new StepException("No matching e-mail found: " + mail);
		}
	}
}