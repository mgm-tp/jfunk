/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.exception;

/**
 * Thrown when input validation fails.
 * 
 * @version $Id$
 */
public class ValidationException extends StepException {

	public ValidationException(final String message) {
		super(message);
	}
}