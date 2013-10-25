package com.mgmtp.jfunk.web.util;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author rnaegele
 */
public class ShortToStringStyle extends ToStringStyle {

	public static final ToStringStyle INSTANCE = new ShortToStringStyle();

	public ShortToStringStyle() {
		this.setUseClassName(false);
		this.setUseIdentityHashCode(false);
	}
}
