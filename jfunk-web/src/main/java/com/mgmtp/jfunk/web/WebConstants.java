/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

/**
 * @author rnaegele
 * @version $Id$
 */
public final class WebConstants {

	public static final String A = "a";
	public static final String ACTION = "action";
	public static final String ALT = "alt";
	public static final String CHECKBOX = "checkbox";
	public static final String CLASS = "class";
	public static final String FOR = "for";
	public static final String FORM = "form";
	public static final String H2 = "h2";
	public static final String HREF = "href";
	public static final String ID = "id";
	public static final String IMAGE = "img";
	public static final String INPUT = "input";
	public static final String LABEL = "label";
	public static final String LI = "li";
	public static final String META = "meta";
	public static final String NAME = "name";
	public static final String OPTION = "option";
	public static final String P = "p";
	public static final String RADIO = "radio";
	public static final String SELECT = "select";
	public static final String SUBMIT = "submit";
	public static final String TABLE = "table";
	public static final String TEXT = "text";
	public static final String TEXTAREA = "textarea";
	public static final String TBODY = "tbody";
	public static final String TD = "td";
	public static final String TH = "th";
	public static final String TITLE = "title";
	public static final String TR = "tr";
	public static final String TYPE = "type";
	public static final String UL = "ul";
	public static final String VALUE = "value";

	public static final int DEFAULT_TIMEOUT = 10;

	/*
	 * Properties for WebDriver
	 */
	public static final String WEBDRIVER_KEY = "webdriver.key";
	public static final String WEBDRIVER_HTMLUNIT = "htmlunit";
	public static final String WEBDRIVER_FIREFOX = "firefox";
	public static final String WEBDRIVER_CHROME = "chrome";
	public static final String WEBDRIVER_INTERNET_EXPLORER = "ie";
	public static final String WEBDRIVER_REMOTE = "remote";
	public static final String WEBDRIVER_IMPLICIT_WAIT_SECONDS = "webdriver.implicit.wait.seconds";
	public static final String WEBDRIVER_DONT_QUIT = "webdriver.dont.quit";

	/*
	 * Properties for the default WebElementFinder
	 */
	public static final String WEF_ENABLED = "wef.enabled";
	public static final String WEF_DISPLAYED = "wef.displayed";
	public static final String WEF_SELECTED = "wef.selected";
	public static final String WEF_TIMEOUT_SECONDS = "wef.timeoutSeconds";
	public static final String WEF_SLEEP_MILLIS = "wef.sleepMillis";

	/*
	 * Properties for the default FormInputHandler
	 */
	public static final String FIH_ENABLED = "fih.enabled";
	public static final String FIH_DISPLAYED = "fih.displayed";
	public static final String FIH_SELECTED = "fih.selected";
	public static final String FIH_TIMEOUT_SECONDS = "fih.timeoutSeconds";
	public static final String FIH_SLEEP_MILLIS = "fih.sleepMillis";

	/*
	 * HTMLUnit constants
	 */
	public static final String HTMLUNIT_AUTO_REFRESH = "htmlunit.autorefresh";
	public static final String HTMLUNIT_CONNECTION_TIMEOUT = "htmlunit.connection.timeout";
	public static final String HTMLUNIT_CREDENTIALS_PREFIX = "htmlunit.credentials";
	public static final String HTMLUNIT_ENABLE_JAVASCRIPT = "htmlunit.enable_javascript";
	public static final String HTMLUNIT_ENABLE_CSS = "htmlunit.enable_css";
	public static final String HTMLUNIT_IGNORE_RESPONSECODE = "htmlunit.ignore.responsecode";
	public static final String HTMLUNIT_REDIRECT = "htmlunit.redirect";
	public static final String HTMLUNIT_REFUSE_COOKIES = "htmlunit.refuse.cookies";
	public static final String HTMLUNIT_VALIDATE_JS = "htmlunit.validate.js";
	public static final String HTMLUNIT_LOG_INCORRECT_CODE = "htmlunit.log.incorrect.code";
	public static final String HTMLUNIT_SAVE_COMPLETE = "htmlunit.save.complete";

	/*
	 * RemoteWebDriver constants
	 */
	public static final String REMOTE_WEBDRIVER_URL = "webdriver.remote.url";

	/*
	 * Properties for W3C markup validation service
	 */
	public static final String W3C_MARKUP_VALIDATION_ENABLE = "w3c.markup.validation.enable";
	public static final String W3C_MARKUP_VALIDATION_LEVEL = "w3c.markup.validation.level";
	public static final String W3C_MARKUP_VALIDATION_URL = "w3c.markup.validation.url";

	public static final String DOUBLECLICK = "ui.doubleclick";

	private WebConstants() {
		// don't allow instantiation
	}
}
