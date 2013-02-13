/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation for injecting configuration values.
 * </p>
 * <p>
 * Usage:
 * 
 * <pre>
 * &#064;InjectConfig(name = &quot;myConfigString&quot;, defaultValue = &quot;foo&quot;)
 * String myConfigItem;
 * 
 * &#064;InjectConfig(name = &quot;myConfigBoolean&quot;)
 * boolean myConfigBoolean;
 * </pre>
 * 
 * </p>
 * 
 * @author rnaegele
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectConfig {
	static final String NO_DEFAULT = "<NO_DEFAULT_VALUE_SPECIFIED>";

	String name();

	String defaultValue() default NO_DEFAULT;
}