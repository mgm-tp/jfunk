package com.mgmtp.jfunk.core.event;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Qualifier annotation for event handler.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, PARAMETER, METHOD })
@Qualifier
public @interface EventHandlers {
	//
}
