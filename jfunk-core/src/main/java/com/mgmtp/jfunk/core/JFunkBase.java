package com.mgmtp.jfunk.core;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.core.event.AfterRunEvent;
import com.mgmtp.jfunk.core.event.BeforeRunEvent;

/**
 * Base class for JFunk.
 * 
 * @version $Id$
 */
public abstract class JFunkBase {

	protected static final Logger LOG = Logger.getLogger(JFunkBase.class);

	private final EventBus eventBus;

	@Inject
	public JFunkBase(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * Special logger which logs the global start and stop time and both the script names and
	 * execution times from the threads.
	 */
	protected static final Logger RESULT_LOG = Logger.getLogger(JFunkConstants.RESULT_LOGGER);

	/**
	 * Executes the jFunk test. A thread pool ({@link ExecutorService}) is created with the number
	 * of configured threads, which handles concurrent script execution.
	 */
	public final void execute() throws Exception {
		eventBus.post(createBeforeRunEvent());
		try {
			doExecute();
		} finally {
			eventBus.post(createAfterRunEvent());
		}
	}

	protected BeforeRunEvent createBeforeRunEvent() {
		return new BeforeRunEvent();
	}

	protected AfterRunEvent createAfterRunEvent() {
		return new AfterRunEvent();
	}

	protected abstract void doExecute() throws Exception;
}