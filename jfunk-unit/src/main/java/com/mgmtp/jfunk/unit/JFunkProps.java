package com.mgmtp.jfunk.unit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a custom properties file with additional Guice modules to be specified for the test class.
 * The file must be relative to jFunk's {@code config} directory.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface JFunkProps {
	/**
	 * The file name of the custom properties file.
	 * 
	 * @return the relative file name
	 */
	String value();
}
