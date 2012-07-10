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
