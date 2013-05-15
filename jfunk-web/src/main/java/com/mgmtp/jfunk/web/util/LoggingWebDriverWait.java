/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * {@link WebDriverWait} decendant that writes a log message before {@link #until(Function)} or
 * {@link #until(Predicate)} is called.
 * 
 * @author rnaegele
 */
public class LoggingWebDriverWait extends WebDriverWait {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final boolean refreshPageAfterUnsuccessfulAttempt;
	private final WebDriver driver;

	public LoggingWebDriverWait(final WebDriver driver, final long timeOutInSeconds) {
		this(driver, false, timeOutInSeconds);
	}

	public LoggingWebDriverWait(final WebDriver driver, final boolean refreshPageAfterUnsuccessfulAttempt,
			final long timeOutInSeconds) {
		this(driver, refreshPageAfterUnsuccessfulAttempt, timeOutInSeconds, DEFAULT_SLEEP_TIMEOUT);
	}

	public LoggingWebDriverWait(final WebDriver driver, final long timeOutInSeconds, final long sleepInMillis) {
		this(driver, false, timeOutInSeconds, sleepInMillis);
	}

	public LoggingWebDriverWait(final WebDriver driver, final boolean refreshPageAfterUnsuccessfulAttempt,
			final long timeOutInSeconds, final long sleepInMillis) {
		super(driver, timeOutInSeconds, sleepInMillis);
		this.driver = driver;
		this.refreshPageAfterUnsuccessfulAttempt = refreshPageAfterUnsuccessfulAttempt;
	}

	@Override
	public <V> V until(final Function<? super WebDriver, V> function) {
		try {
			V result = super.until(function);
			log.info("Successfully waited for: {}", function);
			return result;
		} catch (TimeoutException ex) {
			if (refreshPageAfterUnsuccessfulAttempt) {
				driver.navigate().refresh();
			}
			throw ex;
		}
	}

	@Override
	protected RuntimeException timeoutException(final String message, final Throwable lastException) {
		log.error(message);
		return super.timeoutException(message, lastException);
	}
}
