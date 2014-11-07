package com.mgmtp.jfunk.application.runner.util;

import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import static com.google.common.base.Joiner.on;
import static javafx.application.Platform.runLater;

/**
 * Drains the console queue appending lines to the text area.
 *
 * @author rnaegele
 * @since 4.0.0
 */
public class ConsoleQueueProcessor {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final BlockingQueue<String> consoleQueue;
	private final TextArea console;
	private final int minProcessingsize;

	public ConsoleQueueProcessor(BlockingQueue<String> consoleQueue, TextArea console, int minProcessingsize) {
		this.consoleQueue = consoleQueue;
		this.console = console;
		this.minProcessingsize = minProcessingsize;
	}

	public void processQueue() {
		if (consoleQueue.size() < minProcessingsize) {
			return;
		}
		logger.debug("Draining queue...");
		Collection<String> collection = new LinkedList<>();
		consoleQueue.drainTo(collection);
		logger.trace("size: {}", collection.size());
		if (!collection.isEmpty()) {
			final String text = on("\n").join(collection) + "\n";
			runLater(() -> console.appendText(text));
		}
	}
}
