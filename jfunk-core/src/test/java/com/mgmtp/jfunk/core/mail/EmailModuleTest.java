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

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.SetMultimap;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * @author rnaegele
 */
public class EmailModuleTest {

	@Test
	public void testProvideEmailAddressPools() {
		Configuration config = new Configuration(Charsets.UTF_8);
		config.put("mail.default.user", "defaultuser");
		config.put("mail.default.password", "defaultpass");
		config.put("mail.default.domain", "defdom.com");
		config.put("mail.pool.pool1.test.account.1", "");
		config.put("mail.pool.pool1.test.account.2", "");
		config.put("mail.pool.pool1.test.account.3.user", "foo");
		config.put("mail.pool.pool1.test.account.3.password", "foopass");
		config.put("mail.pool.pool1.test.account.3.address", "foo@bar.de");
		config.put("mail.pool.pool2.testaccount1", "");
		config.put("mail.pool.pool2.testaccount2", "");

		EmailModule module = new EmailModule();
		SetMultimap<String, MailAccount> pools = module.provideEmailAddressPools(config);
		assertThat(pools.size()).isEqualTo(5);
		assertThat(pools.containsEntry("pool1",
				new MailAccount("test.account.1", "test.account.1@defdom.com", "defaultuser", "defaultpass"))).isTrue();
		assertThat(pools.containsEntry("pool1",
				new MailAccount("test.account.2", "test.account.2@defdom.com", "defaultuser", "defaultpass"))).isTrue();
		assertThat(pools.containsEntry("pool1",
				new MailAccount("test.account.3", "foo@bar.de", "foo", "foopass"))).isTrue();
		assertThat(pools.containsEntry("pool2",
				new MailAccount("testaccount1", "testaccount1@defdom.com", "defaultuser", "defaultpass"))).isTrue();
		assertThat(pools.containsEntry("pool2",
				new MailAccount("testaccount2", "testaccount2@defdom.com", "defaultuser", "defaultpass"))).isTrue();
	}
}
