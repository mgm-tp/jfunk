/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Predicate;

/**
 * @author rnaegele
 */
public abstract class LocatorPredicate implements Predicate<WebDriver> {

	protected final By locator;

	protected LocatorPredicate(final By locator) {
		this.locator = locator;
	}
}
