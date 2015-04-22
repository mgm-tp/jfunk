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

import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_CHECK_ACTIVE;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.mgmtp.jfunk.core.config.InjectConfig;
import com.mgmtp.jfunk.core.step.base.DataSetsStep;

/**
 * Base step for e-mail validation. The {@link #execute()} method does nothing if mail-checking is
 * disabled by setting the property {@code mail.check.active} to {@code false}.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public abstract class BaseEmailStep extends DataSetsStep {

	protected final String accountReservationKey;

	@Inject
	protected MailService mailService;

	@InjectConfig(name = MAIL_CHECK_ACTIVE, defaultValue = "true")
	protected boolean mailCheckingActive;

	/**
	 * @param dataSetKey
	 *            the data set key
	 * @param accountReservationKey
	 *            the key under which the mail account to use was reserved
	 */
	public BaseEmailStep(final String dataSetKey, final String accountReservationKey) {
		super(dataSetKey);
		this.accountReservationKey = accountReservationKey;
	}

	/**
	 * Returns if mail checking is disable and otherwise calls {@link #doExecute()}
	 */
	@Override
	public void execute() {
		if (!mailCheckingActive) {
			log.info("Mail checking disabled. Ignoring e-mail of type {}.", getName());
			return;
		}

		doExecute();
	}

	protected abstract void doExecute();

	protected abstract Predicate<MailMessage> createMessagePredicate();
}