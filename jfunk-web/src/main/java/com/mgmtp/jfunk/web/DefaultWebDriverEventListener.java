package com.mgmtp.jfunk.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.openqa.selenium.support.events.WebDriverEventListener;

/**
 * Qualifier annotation for jFunks default {@link WebDriverEventListener}.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
@Qualifier
public @interface DefaultWebDriverEventListener {
	//
}