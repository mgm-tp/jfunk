/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import org.openqa.selenium.WebElement;

/**
 * {@link RuntimeException} related to exceptions with {@link WebElement}s.
 * 
 * @author rnaegele
 * @version $Id$
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
