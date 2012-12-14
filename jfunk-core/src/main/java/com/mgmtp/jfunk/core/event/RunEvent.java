/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.event;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Map;

/**
 * @author rnaegele
 * @version $Id$
 */
public class RunEvent extends AbstractBaseEvent {

	private final Map<String, Object> parameters = newHashMapWithExpectedSize(2);

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Object getParameter(final String name) {
		return parameters.get(name);
	}

	public void addParameter(final String name, final Object value) {
		parameters.put(name, value);
	}
}
