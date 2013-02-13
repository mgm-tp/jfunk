/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step.base;

/**
 * Enum for step modes. {@link Step}s may use this in order to determine whether e. g. to set or to
 * check values.
 * 
 * @version $Id$
 */
public enum StepMode {

	/**
	 * Step mode with the intention that nothing should be done.
	 */
	NONE,

	/**
	 * Step mode with the intention that values should be checked.
	 */
	CHECK_VALUE,

	/**
	 * Step mode with the intention that default values should be checked.
	 */
	CHECK_DEFAULT,

	/**
	 * Step mode with the intention that values should be set.
	 */
	SET_VALUE,
	/**
	 * Editiert den Wert des Parameters und setzt ihn auf den entsprechenden FormData Wert
	 */
	@Deprecated
	EDIT_VALUE,

	/**
	 * @deprecated Elster-specific, to be removed
	 */
	@Deprecated
	SET_EMPTY,

	/**
	 * @deprecated Elster-specific, to be removed
	 */
	@Deprecated
	EDIT_CANCEL;
}