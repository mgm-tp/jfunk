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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Sets.newHashSet;
import static com.mgmtp.jfunk.common.util.Predicates.contains;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_DEBUG;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_DELETE_ALL_ON_RESERVATION;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_SLEEP_MILLIS;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_STORE_PROTOCOL;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_TIMEOUT_SECONDS;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_TRANSPORT_PROTOCOL;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.mail.Session;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mgmtp.jfunk.common.config.ModuleScoped;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;
import com.mgmtp.jfunk.core.mail.StoreManager.FileMessageWrapper;

/**
 * Guice module for e-mail handling. This module must be installed when e-mail support is necessary.
 * 
 * @author rnaegele
 */
public class EmailModule extends BaseJFunkGuiceModule {

	private static final Pattern MAIL_ACCOUNT_START_PATTERN = Pattern.compile("mail[.]pool[.]([^.]+)[.].*");
	private static final Pattern MAIL_ACCOUNT_END_PATTERN = Pattern.compile("user|password|address");
	private static final String MAIL_USER_KEY_TEMPLATE = "${mail.pool.%s.%s.user,%s}";
	private static final String MAIL_PASSWORD_KEY_TEMPLATE = "${mail.pool.%s.%s.password,${mail.default.password}}";
	private static final String MAIL_ADDRESS_KEY_TEMPLATE = "${mail.pool.%s.%s.address,%s@${mail.default.domain}}";

	@Override
	protected void doConfigure() {
		bind(MailService.class);
		bind(MailAccountManager.class);
		bind(SmtpClient.class);

		install(new PrivateModule() {
			@Override
			protected void configure() {
				bind(MutableInt.class).in(ModuleScoped.class);
				bind(MailArchiver.class);
				// expose only MailArchiver
				expose(MailArchiver.class);
			}
		});

		install(new FactoryModuleBuilder().build(StoreManager.Factory.class));
		bindEventHandler().to(MailboxPurger.class);
		bindEventHandler().to(MailAccountReleaser.class);
	}

	@Provides
	@StoreSession
	Properties provideStoreSession(final Configuration config) {
		String protocol = checkNotNull(config.get(MAIL_STORE_PROTOCOL), "Property %s not configured.", MAIL_STORE_PROTOCOL);
		Properties sessionProps = new Properties();
		sessionProps.putAll(filterKeys(config, contains('.' + protocol + '.')));
		sessionProps.setProperty(MAIL_DEBUG, config.get(MAIL_DEBUG, "false"));
		sessionProps.setProperty(MAIL_STORE_PROTOCOL, protocol);
		return sessionProps;
	}

	@Provides
	@TransportSession
	Session provideTransportSession(final Configuration config) {
		String protocol = checkNotNull(config.get(MAIL_TRANSPORT_PROTOCOL), "Property %s not configured.",
				MAIL_TRANSPORT_PROTOCOL);
		Properties sessionProps = new Properties();
		sessionProps.putAll(filterKeys(config, contains('.' + protocol + '.')));
		sessionProps.setProperty(MAIL_DEBUG, config.get(MAIL_DEBUG, "false"));
		sessionProps.setProperty(MAIL_TRANSPORT_PROTOCOL, protocol);

		String user = config.get("mail." + protocol + ".user");
		String password = config.get("mail." + protocol + ".password");

		return !isNullOrEmpty(user) && !isNullOrEmpty(password)
				? Session.getInstance(sessionProps, new MailAuthenticator(user, password))
				: Session.getInstance(sessionProps);
	}

	@Provides
	@MailFolder
	String provideMailFolder(final Configuration config) {
		String protocol = checkNotNull(config.get(MAIL_STORE_PROTOCOL), "Property %s not configured.", MAIL_STORE_PROTOCOL);
		return config.get("mail." + protocol + ".folder", "INBOX");
	}

	@Provides
	@MailTimeoutSeconds
	long provideMailTimeoutSeconds(final Configuration config) {
		return config.getLong(MAIL_TIMEOUT_SECONDS, 60);
	}

	@Provides
	@MailSleepMillis
	long provideMailSleepMillis(final Configuration config) {
		return config.getLong(MAIL_SLEEP_MILLIS, 5000);
	}

	@Provides
	@MailDeleteOnReservation
	boolean provideMailDeleteOnStartup(final Configuration config) {
		return config.getBoolean(MAIL_DELETE_ALL_ON_RESERVATION, true);
	}

	@Provides
	@Singleton
	SetMultimap<String, MailAccount> provideEmailAddressPools(final Configuration config) {
		SetMultimap<String, MailAccount> result = HashMultimap.create();
		Set<String> accountIdCache = newHashSet();

		for (Entry<String, String> entry : config.entrySet()) {
			String key = entry.getKey();

			Matcher matcher = MAIL_ACCOUNT_START_PATTERN.matcher(key);
			if (matcher.matches()) {
				String pool = matcher.group(1);
				String suffix = substringAfterLast(key, pool + '.');

				matcher = MAIL_ACCOUNT_END_PATTERN.matcher(suffix);
				String accountId = matcher.find() ? substringBeforeLast(suffix, ".") : suffix;

				if (accountIdCache.contains(accountId)) {
					continue;
				}

				accountIdCache.add(accountId);

				String user = config.processPropertyValue(format(MAIL_USER_KEY_TEMPLATE, pool, accountId, accountId));
				String password = config.processPropertyValue(format(MAIL_PASSWORD_KEY_TEMPLATE, pool, accountId));
				String address = config.processPropertyValue(format(MAIL_ADDRESS_KEY_TEMPLATE, pool, accountId, accountId));

				result.put(pool, new MailAccount(accountId, address, user, password));
			}
		}

		return result;
	}

	@Provides
	@ScriptScoped
	Table<String, String, FileMessageWrapper> provideMailMessageCache() {
		return HashBasedTable.create();
	}
}
