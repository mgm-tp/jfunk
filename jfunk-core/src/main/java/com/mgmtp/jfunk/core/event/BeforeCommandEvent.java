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
public class BeforeCommandEvent extends CommandEvent {

	public BeforeCommandEvent(final String command, final Object[] params) {
		super(command, params);
	}
}
