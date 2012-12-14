/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.exception;

/**
 * This exception is thrown when an error occurred while handling mails e.g. connecting to a mail
 * server or accessing mails.
 * 
 * @version $Id$
 */
public class MailException extends RuntimeException {
	public MailException(final String message) {
		super(message);
	}

	public MailException(final String message, final Throwable e) {
		super(message, e);
	}
}