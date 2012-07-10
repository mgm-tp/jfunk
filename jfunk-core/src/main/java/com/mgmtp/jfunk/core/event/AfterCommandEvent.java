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
