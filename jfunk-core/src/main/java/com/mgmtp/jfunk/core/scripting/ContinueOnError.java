package com.mgmtp.jfunk.core.scripting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mgmtp.jfunk.core.step.base.ComplexStep;

/**
 * Marker annotation for {@link ComplexStep}. If a {@link ComplexStep} is annotated with this
 * annotation, exceptions on any single child step are caught.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ContinueOnError {
	//
}
