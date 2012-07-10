package com.mgmtp.jfunk.core.reporting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * {@link TestModule}s are reported automatically but can be excluded from reports by annotating the
 * with {@code @Reported(false)}. Steps may be included in reports by annotating the with this
 * annotation.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Reported {

	/**
	 * Specifies whether or not the object is to be included in a report.
	 * 
	 * @return {@code true} if the object is to be included in a report, {@code false} otherwise
	 */
	boolean value() default true;
}
