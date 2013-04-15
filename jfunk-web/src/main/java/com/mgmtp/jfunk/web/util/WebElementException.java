/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import org.openqa.selenium.WebElement;

/**
 * {@link RuntimeException} related to exceptions with {@link WebElement}s.
 * 
 * @author rnaegele
 */
public class WebElementException extends RuntimeException {

	public WebElementException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public WebElementException(final String message) {
		super(message);
	}

	public WebElementException(final Throwable cause) {
		super(cause);
	}
}
