/*
 * Copyright (c) 2014 mgm technology partners GmbH
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

import javax.mail.Message;

import com.google.common.base.Function;

/**
 * Functions for mail messages.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class MessageFunctions {

	/**
	 * Creates a function that transforms a {@link Message} object to a {@link MailMessage} object.
	 * 
	 * @return the function
	 */
	public static Function<Message, MailMessage> toMailMessage() {
		return new Function<Message, MailMessage>() {
			@Override
			public MailMessage apply(final Message input) {
				return MailMessage.fromMessage(input);
			}
		};
	}
}
