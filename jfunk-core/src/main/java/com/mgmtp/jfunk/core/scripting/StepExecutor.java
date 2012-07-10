package com.mgmtp.jfunk.core.scripting;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Injector;
import com.mgmtp.jfunk.core.event.AfterStepEvent;
import com.mgmtp.jfunk.core.event.BeforeStepEvent;
import com.mgmtp.jfunk.core.event.StepEvent;
import com.mgmtp.jfunk.core.step.base.Step;

/**
 * This class' executeStep method is responsible for calling a step's execute method and posting
 * {@link StepEvent}s to the {@link EventBus}. Member injection is perform on the {@link Step}
 * instance before execution.
 * 
 * @version $Id$
 */
@Singleton
public class StepExecutor {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Injector injector;
	private final EventBus eventBus;

	@Inject
	public StepExecutor(final Injector injector, final EventBus eventBus) {
		this.injector = injector;
		this.eventBus = eventBus;
	}

	public void executeStep(final Step step) {
		executeStep(step, -1, true);
	}

	public void executeStep(final Step step, final boolean triggerEvents) {
		executeStep(step, -1, triggerEvents);
	}

	public void executeStep(final Step step, final int index) {
		executeStep(step, index, true);

	}

	public void executeStep(final Step step, final int index, final boolean triggerEvents) {
		// perform DI on step
		injector.injectMembers(step);
		Throwable th = null;

		try {
			if (triggerEvents) {
				eventBus.post(new InternalBeforeStepEvent(step, index));
				eventBus.post(new BeforeStepEvent(step, index));
			}
			step.execute();
		} catch (RuntimeException ex) {
			th = ex;

			if (handleThrowable(step, ex)) {
				throw ex;
			}
		} catch (AssertionError err) {
			th = err;

			if (handleThrowable(step, err)) {
				throw err;
			}
		} finally {
			if (triggerEvents) {
				eventBus.post(new AfterStepEvent(step, index, th));
				eventBus.post(new InternalAfterStepEvent(step, index, th));
			}
		}
	}

	private boolean handleThrowable(final Step step, final Throwable th) {
		if (step.getClass().isAnnotationPresent(ContinueOnError.class)) {
			log.error("Exception executing step: " + step, th);
			return false;
		}
		return true;
	}
}