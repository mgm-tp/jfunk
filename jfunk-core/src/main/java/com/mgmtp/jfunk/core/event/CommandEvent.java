/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.event;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

/**
 * @author rnaegele
 * @version $Id$
 */
public abstract class CommandEvent extends AbstractBaseEvent {

	private final String command;
	private final List<Object> params;

	public CommandEvent(final String command, final Object[] params) {
		this.command = command;
		this.params = Collections.unmodifiableList(asList(params));
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return the params
	 */
	public List<Object> getParams() {
		return params;
	}
}
