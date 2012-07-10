package com.mgmtp.jfunk.core.exception;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * This exception is thrown when the execution of a {@link Step} produces an error.
 * 
 * @version $Id$
 */
public class StepException extends RuntimeException {
	public StepException(final String message) {
		super(message);
	}

	public StepException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public static StepException wrapThrowable(final String message, final Throwable th) {
		Throwable localTh = th;
		while (localTh instanceof StepException) {
			Throwable cause = localTh.getCause();
			if (cause == null) {
				break;
			}
			localTh = cause;
		}
		return new StepException(message + ". Original error: " + localTh.getMessage(), localTh);
	}
}