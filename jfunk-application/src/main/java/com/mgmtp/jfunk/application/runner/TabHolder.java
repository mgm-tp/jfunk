package com.mgmtp.jfunk.application.runner;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

import java.util.concurrent.BlockingQueue;

/**
 * @author rnaegele
 */
public class TabHolder {

	private final Tab tab;
	private final TextArea console;
	private final Executor executor;
	private final ExecuteWatchdog watchdog;
	private final BlockingQueue<String> consoleQueue;

	public TabHolder(final Tab tab, final TextArea console, final Executor executor, final ExecuteWatchdog watchdog,
			BlockingQueue<String> consoleQueue) {
		this.tab = tab;
		this.console = console;
		this.executor = executor;
		this.watchdog = watchdog;
		this.consoleQueue = consoleQueue;
	}

	public Tab getTab() {
		return tab;
	}

	public TextArea getConsole() {
		return console;
	}

	public ExecuteWatchdog getWatchdog() {
		return watchdog;
	}

	public BlockingQueue<String> getConsoleQueue() {
		return consoleQueue;
	}

	public Executor getExecutor() {
		return executor;
	}
}
