/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.exception;

/**
 * This exception is thrown when accessing generator constraints by id while the specified id does
 * not exist.
 * 
 * @version $Id$
 */
public class IdNotFoundException extends RuntimeException {
	public IdNotFoundException(final String id) {
		super("Could not find id=" + id);
	}
}