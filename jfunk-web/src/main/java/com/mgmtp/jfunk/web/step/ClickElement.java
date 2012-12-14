/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.mgmtp.jfunk.core.config.InjectConfig;
import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.web.WebConstants;

/**
 * This step looks for a element and clicks on it. If the configuration key
 * {@link WebConstants#DOUBLECLICK} is set to {@code true}, double-clicks are performed instead of
 * single clicks.
 * 
 * @version $Id$
 */
@Deprecated
public class ClickElement extends WebDriverStep {
	private final By by;
	private final boolean ignoreIfNotFound;

	@InjectConfig(name = WebConstants.DOUBLECLICK, defaultValue = "false")
	boolean doubleClick;

	/**
	 * The element to click is detected using a {@link By} object. See Javadoc for the various
	 * methods offered.
	 * 
	 * @param by
	 *            used for locating the link
	 * @param test
	 *            param no longer used
	 */
	@Deprecated
	public ClickElement(final By by, final TestModule test) {
		this(by);
	}

	/**
	 * The element to click is detected using a {@link By} object. See Javadoc for the various
	 * methods offered.
	 * 
	 * @param by
	 *            used for locating the link
	 * @param ignoreIfNotFound
	 *            if {@code true}, no exception is thrown if the element is not found
	 * @param test
	 *            param no longer used
	 */
	@Deprecated
	public ClickElement(final By by, final boolean ignoreIfNotFound, final TestModule test) {
		this(by, ignoreIfNotFound);
	}

	/**
	 * The element to click is detected using a {@link By} object. See Javadoc for the various
	 * methods offered.
	 * 
	 * @param by
	 *            used for locating the link
	 */
	public ClickElement(final By by) {
		this(by, false);
	}

	/**
	 * The element to click is detected using a {@link By} object. See Javadoc for the various
	 * methods offered.
	 * 
	 * @param by
	 *            used for locating the link
	 * @param ignoreIfNotFound
	 *            if {@code true}, no exception is thrown if the element is not found
	 */
	public ClickElement(final By by, final boolean ignoreIfNotFound) {
		this.by = by;
		this.ignoreIfNotFound = ignoreIfNotFound;
	}

	/**
	 * @return the by
	 */
	public By getBy() {
		return by;
	}

	@Override
	public void execute() {
		log.info("Searching for " + this);
		final WebDriverWait wait = new WebDriverWait(getWebDriver(), WebConstants.DEFAULT_TIMEOUT);

		try {
			final WebElement webElement = wait.until(new Function<WebDriver, WebElement>() {
				@Override
				public WebElement apply(final WebDriver input) {
					WebElement el;
					try {
						el = input.findElement(getBy());
					} catch (RuntimeException e) {
						// cannot catch NoSuchElementException directly because it is cast to RuntimeException in WebDriver code
						if (ignoreIfNotFound && e instanceof NoSuchElementException) {
							return null;
						}
						throw new StepException("Could not find element", e);
					}
					if (el.isDisplayed() && el.isEnabled()) {
						return el;
					}
					throw new StepException(el + " is invisible or disabled");
				}
			});

			if (log.isDebugEnabled()) {
				log.debug("Found " + this + " - trying to click on it ...");
			}
			if (doubleClick) {
				new Actions(getWebDriver()).doubleClick(webElement).perform();
				if (log.isDebugEnabled()) {
					log.debug("... doubleclicked on element.");
				}
			} else {
				webElement.click();
				if (log.isDebugEnabled()) {
					log.debug("... clicked on element.");
				}
			}
		} catch (TimeoutException ex) {
			if (ignoreIfNotFound) {
				log.warn("Ignoring not found element: " + getBy());
			} else {
				throw ex;
			}
		}
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("by", getBy());
		tsb.append("doubleClick", doubleClick);
		tsb.append("ignoreIfNotFound", ignoreIfNotFound);
		return tsb.toString();
	}
}