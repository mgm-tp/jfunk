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
package com.mgmtp.jfunk.web.util;

import java.util.List;

import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

/**
 * {@link WebDriverWait} decendant that writes a log message before {@link #until(Function)} or
 * {@link #until(Predicate)} is called.
 * 
 * @author rnaegele
 */
public class LoggingWebDriverWait extends WebDriverWait {
	private static final List<Class<? extends Throwable>> IGNORED_EXCEPTIONS = ImmutableList.<Class<? extends Throwable>>of(
			NotFoundException.class, WebElementException.class, StaleElementReferenceException.class);

	private final Logger log = LoggerFactory.getLogger(getClass());

	public LoggingWebDriverWait(final WebDriver driver, final long timeOutInSeconds) {
		this(driver, timeOutInSeconds, WebDriverWait.DEFAULT_SLEEP_TIMEOUT);
	}

	public LoggingWebDriverWait(final WebDriver driver, final long timeOutInSeconds, final long sleepInMillis) {
		super(driver, timeOutInSeconds, sleepInMillis);
		ignoreAll(IGNORED_EXCEPTIONS);
	}

	@Override
	public <V> V until(final Function<? super WebDriver, V> function) {
		log.info("Waiting for {}", function);
		V result = super.until(function);
		log.info("Successfully waited for {}", function);
		return result;
	}

	@Override
	protected RuntimeException timeoutException(final String message, final Throwable lastException) {
		log.error(message);
		return super.timeoutException(message, lastException);
	}
}
