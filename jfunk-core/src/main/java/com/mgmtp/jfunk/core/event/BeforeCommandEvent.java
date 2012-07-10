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
