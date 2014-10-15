package com.mgmtp.jfunk.application.runner.exec;

import com.mgmtp.jfunk.application.runner.util.ConsoleQueueProcessor;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

import static com.mgmtp.jfunk.application.runner.util.UiUtils.createImageView;

public class TabExecuteResultHandler implements ExecuteResultHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Tab tab;
	private final ScheduledFuture<?> future;
	private final ConsoleQueueProcessor queueProcessor;

	public TabExecuteResultHandler(final Tab tab, final ScheduledFuture<?> future, final ConsoleQueueProcessor queueProcessor) {
		this.tab = tab;
		this.future = future;
		this.queueProcessor = queueProcessor;
	}

	@Override
	public void onProcessComplete(final int exitValue) {
		Platform.runLater(new Runnable() {
			public void run() {
				tab.setGraphic(createImageView("com/famfamfam/silk/accept.png"));
				tab.setClosable(true);
				future.cancel(true);

				// we process the queue after canceling, otherwise it might happen the we
				// do not any process output if a process terminates very fast
				queueProcessor.processQueue();
			}
		});
	}

	@Override
	public void onProcessFailed(final ExecuteException ex) {
		Platform.runLater(new Runnable() {
			public void run() {
				tab.setGraphic(createImageView("com/famfamfam/silk/exclamation.png"));
				tab.setClosable(true);
				future.cancel(true);

				// we process the queue after canceling, otherwise it might happen the we
				// do not any process output if a process fails very fast
				queueProcessor.processQueue();

				logger.error(ex.getMessage(), ex);
			}
		});
	}
}
