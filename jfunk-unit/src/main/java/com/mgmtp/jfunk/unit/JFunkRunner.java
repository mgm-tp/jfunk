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
package com.mgmtp.jfunk.unit;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.core.mail.MailAccount;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.reporting.Reporter;
import com.mgmtp.jfunk.core.scripting.ScriptContext;

/**
 * Allows "jFunk scripting" in unit tests. An instance of this class can be injected into a unit
 * test class.
 * 
 * @author rnaegele
 */
@Singleton
@ThreadSafe
public final class JFunkRunner {

	private final Provider<ScriptContext> scriptContextProvider;

	/**
	 * Creates a new instance.
	 * 
	 * @param scriptContextProvider
	 *            provides the correctly scoped {@link ScriptContext}
	 */
	@Inject
	JFunkRunner(final Provider<ScriptContext> scriptContextProvider) {
		this.scriptContextProvider = scriptContextProvider;
	}

	/**
	 * @see ScriptContext#copyFormEntry(String, String, String, String)
	 */
	public void copyFormEntry(final String srcDataSetKey, final String srcKey, final String destDataSetKey, final String destKey) {
		scriptContextProvider.get().copyFormEntry(srcDataSetKey, srcKey, destDataSetKey, destKey);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#load(String, boolean)
	 */
	public void load(final String propertiesFile, final boolean preserveExisting) {
		scriptContextProvider.get().load(propertiesFile, preserveExisting);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#generate(String)
	 * @deprecated use {@link #prepareNextDataSet(String)}
	 */
	@Deprecated
	public void generate(final String dataSetKey) {
		scriptContextProvider.get().generate(dataSetKey);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#prepareNextDataSet(String)
	 */
	public void prepareNextDataSet(final String dataSetKey) {
		scriptContextProvider.get().prepareNextDataSet(dataSetKey);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#registerReporter(Reporter)
	 */
	public Reporter registerReporter(final Reporter reporter) {
		return scriptContextProvider.get().registerReporter(reporter);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#get(String)
	 */
	public String get(final String configKey) {
		return scriptContextProvider.get().get(configKey);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#resetFixedData(String, String)
	 */
	public void resetFixedData() {
		scriptContextProvider.get().resetFixedData(null, null);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#resetFixedData(String, String)
	 */
	public void resetFixedData(final String dataSetKey) {
		scriptContextProvider.get().resetFixedData(dataSetKey, null);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#resetFixedData(String, String)
	 */
	public void resetFixedData(final String dataSetKey, final String entryKey) {
		scriptContextProvider.get().resetFixedData(dataSetKey, entryKey);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#run(String)
	 */
	public void run(final String testModuleClassName) {
		scriptContextProvider.get().run(testModuleClassName);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#run(TestModule)
	 */
	public void run(final TestModule testModule) {
		scriptContextProvider.get().run(testModule);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#run(TestModule)
	 */
	public void runModule() {
		ScriptContext scriptContext = scriptContextProvider.get();
		scriptContext.run(scriptContext.get("${" + JFunkConstants.TESTMODULE_CLASS + "}"));
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#resetDataSource()
	 */
	public void resetDataSource() {
		scriptContextProvider.get().resetDataSource();
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#setFormEntry(String, String, String)
	 */
	public void setFormEntry(final String dataSetKey, final String entryKey, final String value) {
		scriptContextProvider.get().setFormEntry(dataSetKey, entryKey, value);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#set(String, String)
	 */
	public void set(final String key, final String value) {
		scriptContextProvider.get().set(key, value);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#setNow(String, String)
	 */
	public void setNow(final String key, final String value) {
		scriptContextProvider.get().setNow(key, value);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#reserveMailAccount()
	 */
	public MailAccount reserveMailAccount() {
		return scriptContextProvider.get().reserveMailAccount();
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#reserveMailAccount(java.lang.String)
	 */
	public MailAccount reserveMailAccount(final String accountReservationKey) {
		return scriptContextProvider.get().reserveMailAccount(accountReservationKey);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#reserveMailAccount(java.lang.String,
	 *      java.lang.String)
	 */
	public MailAccount reserveMailAccount(final String accountReservationKey, final String pool) {
		return scriptContextProvider.get().reserveMailAccount(accountReservationKey, pool);
	}

	/**
	 * 
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#releaseAllMailAccountsForThread()
	 */
	public void releaseAllMailAccountsForThread() {
		scriptContextProvider.get().releaseAllMailAccountsForThread();
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#releaseMailAccountForThread(com.mgmtp.jfunk.core.mail.MailAccount)
	 */
	public void releaseMailAccountForThread(final MailAccount account) {
		scriptContextProvider.get().releaseMailAccountForThread(account);
	}

	/**
	 * @see com.mgmtp.jfunk.core.scripting.ScriptContext#releaseMailAccountForThread(java.lang.String)
	 */
	public void releaseMailAccountForThread(final String accountReservationKey) {
		scriptContextProvider.get().releaseMailAccountForThread(accountReservationKey);
	}
}
