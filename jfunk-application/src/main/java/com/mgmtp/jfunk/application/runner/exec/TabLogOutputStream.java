package com.mgmtp.jfunk.application.runner.exec;

import org.apache.commons.exec.LogOutputStream;

import java.util.Queue;

/**
 * LogOutputStream that adds processed lines to a queue.
 *
 * @author rnaegele
 * @since 3.1.0
 */
public class TabLogOutputStream extends LogOutputStream {

	private final Queue<String> queue;

	/**
	 * @param queue
	 * 		a queue to add processed lines to
	 */
	public TabLogOutputStream(Queue<String> queue) {
		this.queue = queue;
	}

	/**
	 * Offers the given {@code line} to the internal queue.
	 *
	 * @param line
	 * 		the line to add to the queue
	 * @param level
	 * 		not used
	 */
	@Override
	protected void processLine(final String line, final int level) {
		queue.offer(line);
	}
}
