package com.mgmtp.jfunk.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Base class for events.
 * 
 * @author rnaegele
 * @version $Id$
 */
abstract class AbstractBaseEvent {

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
