/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common;

import com.mgmtp.jfunk.common.util.SaveOutput;

/**
 * @version $Id$
 */
public final class JFunkConstants {
	public static final String JFUNK_PROPERTIES = "jfunk.properties";
	public static final String SCRIPT_PROPERTIES = "script.properties";

	public static final String ARCHIVING_MODE = "archiving.mode";
	public static final String ARCHIVING_MODE_ALL = "all";
	public static final String ARCHIVING_MODE_ERROR = "error";
	public static final String ARCHIVING_MODE_NONE = "none";
	public static final String LASTPAGE_HTML = "lastpage.html";
	public static final String STACKTRACE_LOG = "stacktrace.log";

	public static final String SYSTEM_PROPERTIES = "system.properties";
	public static final String ZIP_FILE_SUFFIX = ".zip";

	public static final String STEP = "step";
	public static final String EXECUTION_MODE = "execution.mode";
	public static final String EXECUTION_MODE_START = "start";
	public static final String EXECUTION_MODE_FINISH = "finish";
	public static final String EXECUTION_MODE_ALL = "all";

	public static final String ARCHIVE_DIR = "archive.dir";
	public static final String ARCHIVE_DIR_DEFAULT = "testruns";
	public static final String ARCHIVE_FILE = "archive.file";

	public static final String JAVAX_NET_SSL_TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	public static final String JAVAX_NET_SSL_TRUST_STORE = "javax.net.ssl.trustStore";
	public static final String JAVAX_NET_SSL_TRUST_STORE_TYPE = "javax.net.ssl.trustStoreType";
	public static final String JAVAX_NET_SSL_KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	public static final String JAVAX_NET_SSL_KEY_STORE = "javax.net.ssl.keyStore";
	public static final String JAVAX_NET_SSL_KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";

	public static final String OK = "ok";
	public static final String ERROR = "error";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String CURRENT_MODULE_RESULT = "current.module.result";
	public static final String CURRENT_MODULE_ERROR = "current.module.error";
	public static final String CURRENT_MODULE_NAME = "current.module.name";
	public static final String TESTMODULE_CLASS = "testmodule.class";

	public static final String USER_DIR = "user.dir";

	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String DATA_SOURCE_NAME = "dataSource.name";
	public static final String FORM_DATA_PREFIX = "formData.";
	public static final String FORM_PROPERTIES_ENDING = ".form.properties";
	public static final String PROPERTIES_ENDING = ".properties";

	/**
	 * Setting the value of this property to {@code true} results in deactivating all optional
	 * constraints so that the underlying values are always created
	 */
	public static final String IGNORE_CONSTRAINT_OPTIONAL = "ignore.constraint.optional";

	/**
	 * Used for initialization of random number generator
	 */
	public static final String RANDOM_SEED = "random.seed";

	/**
	 * Name of result logger
	 */
	public static final String RESULT_LOGGER = "result";

	public static final String UNIT_TEST_METHOD = "unit.test.method";
	public static final String SCRIPT_NAME = "script.name";
	public static final String SCRIPT_DIR = "script.dir";

	/**
	 * Indexed form entry keys use the following separator preceding the index
	 */
	public static final String INDEXED_KEY_SEPARATOR = "#";

	/*
	 * Properties for archive contents
	 */
	public static final String ARCHIVE_INCLUDE = "archive.include.";
	public static final String ARCHIVE_INCLUDE_HTML = ARCHIVE_INCLUDE + SaveOutput.HTML.getIdentifier();
	public static final String ARCHIVE_INCLUDE_PNG = ARCHIVE_INCLUDE + SaveOutput.PNG.getIdentifier();

	/**
	 * Thread-ID is written to test properties. This id is set explicitly to a two-digit integer
	 * value in JFunk.createExecutorService().
	 */
	public static final String THREAD_ID = "thread.id";

	public static final String TESTMERKER = "testmerker";

	private JFunkConstants() {
		// don't allow instantiation
	}
}
