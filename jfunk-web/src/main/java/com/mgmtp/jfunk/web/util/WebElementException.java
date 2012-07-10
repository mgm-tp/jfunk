package com.mgmtp.jfunk.web.util;

import org.openqa.selenium.WebElement;

/**
 * {@link RuntimeException} related to exceptions with {@link WebElement}s.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class WebElementException extends RuntimeException {

	private final WebElement element;

	/**
	 * @param element
	 *            the {@link WebElement} related to this exception
	 * @param message
	 *            the exception message
	 */
	public WebElementException(final WebElement element, final String message) {
		super(message);
		this.element = element;
	}

	/**
	 * @return the element
	 */
	public WebElement getElement() {
		return element;
	}
}
