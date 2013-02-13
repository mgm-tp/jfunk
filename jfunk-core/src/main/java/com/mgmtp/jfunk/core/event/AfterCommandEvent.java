/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.event;

/**
 * @author rnaegele
 * @version $Id$
 */
public class AfterCommandEvent extends CommandEvent {

	private final boolean success;

	public AfterCommandEvent(final String command, final Object[] params, final boolean success) {
		super(command, params);
		this.success = success;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}
}
