/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.htmlunit;

import com.gargoylesoftware.htmlunit.Page;

/**
 * Container object for JavaScript alerts.
 * 
 * @version $Id$
 */
public class JavaScriptAlert {
	private final Page page;
	private final String message;

	public JavaScriptAlert(final Page page, final String message) {
		this.page = page;
		this.message = message;
	}

	public Page getPage() {
		return this.page;
	}

	public String getMessage() {
		return this.message;
	}
}