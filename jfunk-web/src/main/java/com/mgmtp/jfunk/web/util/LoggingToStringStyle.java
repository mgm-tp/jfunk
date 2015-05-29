package com.mgmtp.jfunk.web.util;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author rnaegele
 */
public final class LoggingToStringStyle extends ToStringStyle {

	public static final LoggingToStringStyle INSTANCE = new LoggingToStringStyle();

	LoggingToStringStyle() {
		this.setUseClassName(false);
		this.setUseIdentityHashCode(false);
		this.setUseFieldNames(true);
		this.setContentStart("");
		this.setContentEnd("");
	}
}