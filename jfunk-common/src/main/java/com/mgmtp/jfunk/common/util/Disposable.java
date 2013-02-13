/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.util;

/**
 * Interface for some disposal logic. Implementers of this interface may be registered to be called
 * either after module or after script execution.
 * 
 * @author rnaegele
 * @version $Id$
 */
public interface Disposable {

	/**
	 * Performs some disposal logic.
	 */
	void dispose();
}
