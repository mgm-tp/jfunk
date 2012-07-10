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