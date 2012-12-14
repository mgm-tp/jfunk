/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.exception;

/**
 * JFunk's custom {@link RuntimeException}.
 * 
 * @version $Id$
 */
public class JFunkException extends RuntimeException {

	public JFunkException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public JFunkException(final String message) {
		super(message);
	}

	public JFunkException(final Throwable cause) {
		super(cause);
	}
}