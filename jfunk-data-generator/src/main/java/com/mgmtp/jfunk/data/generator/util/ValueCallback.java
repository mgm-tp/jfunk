package com.mgmtp.jfunk.data.generator.util;

import com.mgmtp.jfunk.data.generator.constraint.Constraint;

/**
 * Used by a {@link Constraint} during initialisation to determine if a fixed value is set. If a
 * fixed value is set no other value will be generated until
 * {@link Constraint#setValueCallback(ValueCallback)} is set to {@code null}. A fixed value survives
 * {@link Constraint#resetValues()}.
 * 
 * @version $Id$
 */
public interface ValueCallback {

	/**
	 * @return a fixed value or {@code null} if no fixed value is set.
	 */
	String getImmutableValue();
}