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

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author rnaegele
 * @since 3.1.0
 */
class MailAuthenticator extends Authenticator {

	private final String user;
	private final String password;

	MailAuthenticator(final String user, final String password) {
		this.user = user;
		this.password = password;
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password);
	}

	String getUser() {
		return user;
	}

	String getPassword() {
		return password;
	}
}
