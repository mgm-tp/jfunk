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
package com.mgmtp.jfunk.core.mail;

import com.google.inject.Provides;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;

/**
 * Guice module for e-mail handling. This module must be installed when e-mail support is necessary.
 * 
 * @author rnaegele
 */
public class EmailModule extends BaseJFunkGuiceModule {

	@Override
	protected void doConfigure() {
		bind(EmailParser.class);
		bind(EmailParserFactory.class);
		bind(MailHandler.class).to(DefaultMailHandler.class);
		bindEventHandler().to(EmailEventHandler.class);
		bindScriptScopedDisposable().to(DefaultMailHandler.class);
	}

	@Provides
	MailAccount provideMailAccount(final Configuration config, final MailHandler mailHandler) {
		return mailHandler.getMailAccount(config);
	}
}
