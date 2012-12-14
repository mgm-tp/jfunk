/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * @author rnaegele
 * @version $Id: $
 */
public abstract class BasePredicate<T, R> implements Predicate<T> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public final boolean apply(final T input) {
		boolean result = doApply(input);
		log.info("Predicate {}: {}", result ? "successful" : "failed", this);
		return result;
	}

	protected abstract boolean doApply(final T input);

	public R getResult() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
