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
package com.mgmtp.jfunk.web.step;

import javax.inject.Inject;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import com.mgmtp.jfunk.core.config.InjectConfig;
import com.mgmtp.jfunk.core.step.base.BaseStep;
import com.mgmtp.jfunk.web.WebConstants;
import com.mgmtp.jfunk.web.util.WebDriverTool;

/**
 * This step looks for a element and clicks on it. If the configuration key
 * {@link WebConstants#DOUBLECLICK} is set to {@code true}, double-clicks are performed instead of
 * single clicks.
 */
@Deprecated
public class ClickElement extends BaseStep {
	private final By by;
	private final boolean ignoreIfNotFound;

	@InjectConfig(name = WebConstants.DOUBLECLICK, defaultValue = "false")
	boolean doubleClick;

	@Inject
	WebDriverTool wdt;

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
		try {
			if (doubleClick) {
				wdt.doubleClick(by);
			} else {
				wdt.click(by);
			}
		} catch (TimeoutException ex) {
			if (ignoreIfNotFound) {
				log.warn("Ignoring not found element: " + getBy());
			} else {
				throw ex;
			}
		}
	}
}