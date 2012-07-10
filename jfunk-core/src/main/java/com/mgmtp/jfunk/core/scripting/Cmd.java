package com.mgmtp.jfunk.core.scripting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Qualifier annotation for command methods.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Qualifier
public @interface Cmd {
	//
}
