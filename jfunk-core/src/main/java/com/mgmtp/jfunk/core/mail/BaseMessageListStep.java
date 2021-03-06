/*
 * Copyright (c) 2015 mgm technology partners GmbH
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

import java.util.List;

import com.google.common.base.Predicate;

/**
 * Base step for validating a list of e-mail messages. The {@link #execute()} method does nothing if
 * mail-checking is disabled by setting the property {@code mail.check.active} to {@code false}.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public abstract class BaseMessageListStep extends BaseEmailStep {

	/**
	 * @param dataSetKey
	 *            the data set key
	 * @param accountReservationKey
	 *            the key under which the mail account to use was reserved
	 */
	public BaseMessageListStep(final String dataSetKey, final String accountReservationKey) {
		super(dataSetKey, accountReservationKey);
	}

	/**
	 * Returns the number of e-mail messages to be retrieved applying the {@link Predicate} returned
	 * by {@link #createMessagePredicate()}.
	 * 
	 * @return the expected number of e-mail messages
	 */
	protected abstract int getExpectedMessagesCount();

	/**
	 * Retrieves a list of e-mail messages matching the patterns specified in the constructor. If
	 * the messages are successfully retrieved, they are passed to {@link #validateMessages(List)}
	 * for further validation.
	 */
	@Override
	protected void doExecute() {
		Predicate<MailMessage> condition = createMessagePredicate();
		List<MailMessage> messages = mailService.findMessages(accountReservationKey, condition, getExpectedMessagesCount());
		validateMessages(messages);
	}

	protected abstract void validateMessages(List<MailMessage> messages);
}