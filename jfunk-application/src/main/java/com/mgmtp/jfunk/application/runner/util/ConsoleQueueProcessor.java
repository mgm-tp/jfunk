package com.mgmtp.jfunk.application.runner.util;

import com.google.common.base.Joiner;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

/**
 * @author rnaegele
 * @since 3.1.0
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
			logger.debug("too small");
			return;
		}
		logger.debug("Draining queue...");
		Collection<String> collection = new LinkedList<String>();
		consoleQueue.drainTo(collection);
		logger.debug("size: {}", collection.size());
		if (!collection.isEmpty()) {
			final String text = Joiner.on("\n").join(collection);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					console.appendText(text + "\n");
				}
			});
		}
	}
}
