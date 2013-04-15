/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.random;

import com.google.common.collect.Lists;

/**
 * Extension of {@link RandomCollection}. The collection only contains {@link Boolean#TRUE} and
 * {@link Boolean#FALSE}.
 * 
 */
public class Choice extends RandomCollection<Boolean> {

	public Choice(final MathRandom random) {
		super(random, Lists.newArrayList(Boolean.TRUE, Boolean.FALSE));
	}
}