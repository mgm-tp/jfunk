/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.unit;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.mgmtp.jfunk.common.JFunkConstants;
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
}
