/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.exception;

/**
 * Thrown when any initialisation process failed
 * 
 * @version $Id$
 */
@Deprecated
public class InitializationException extends RuntimeException {
	public InitializationException(final String message) {
		super(message);
	}

	public InitializationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InitializationException(final Throwable cause) {
		super(cause);
	}
}