/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Checks if a single HTML element is present on the current page.
 * 
 * @version $Id$
 */
public class Check4Element extends WebDriverStep {
	protected final By by;
	protected final boolean mustExist;

	/**
	 * @param by
	 *            By means of this value the HTML element is searched after
	 * @param test
	 *            param no longer used
	 */
	@Deprecated
	public Check4Element(final By by, final TestModule test) {
		this(by);
	}

	/**
	 * @param by
	 *            By means of this value the HTML element is searched after
	 * @param mustExist
	 *            if {@code true} the element has to exist on the page, if {@code false} the element
	 *            must not exist.
	 * @param test
	 *            param no longer used
	 */
	@Deprecated
	public Check4Element(final By by, final boolean mustExist, final TestModule test) {
		this(by, mustExist);
	}

	/**
	 * @param by
	 *            By means of this value the HTML element is searched after
	 */
	public Check4Element(final By by) {
		this(by, true);
	}

	/**
	 * @param by
	 *            By means of this value the HTML element is searched after
	 * @param mustExist
	 *            if {@code true} the element has to exist on the page, if {@code false} the element
	 *            must not exist.
	 */
	public Check4Element(final By by, final boolean mustExist) {
		this.by = by;
		this.mustExist = mustExist;
	}

	/**
	 * @throws StepException
	 *             if element specified by {@link By} object in the constructor cannot be found
	 */
	@Override
	public void execute() throws StepException {
		if (mustExist) {
			log.info("Checking if " + this + " is present");
		} else {
			log.info("Checking that " + this + " does not exist");
		}

		List<WebElement> webElements = getWebDriver().findElements(by);
		if (webElements.isEmpty()) {
			if (mustExist) {
				throw new StepException("Could not find any matching element");
			}
		} else {
			// webElements is not empty
			if (!mustExist) {
				throw new StepException("Matching element could be found although mustExist=false");
			}
			/*
			 * If the search using the By object does find more than one matching element we are
			 * looping through all elements if we find at least one which matches the criteria
			 * below. If not, an exception is thrown.
			 */
			for (WebElement element : webElements) {
				if (element.isDisplayed()) {
					if (element.isEnabled()) {
						if (!mustExist) {
							throw new StepException("Matching element could be found although mustExist=false");
						}
						return;
					}
				}
			}
		}

		throw new StepException("All elements matching by=" + by + " were either invisible or disabled");
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("by", by);
		return tsb.toString();
	}
}