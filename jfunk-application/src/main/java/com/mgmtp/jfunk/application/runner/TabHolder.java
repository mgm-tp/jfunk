package com.mgmtp.jfunk.application.runner;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Watchdog;

/**
 * @author rnaegele
 */
public class TabHolder {

	private Tab tab;
	private TextArea console;
	private ExecuteWatchdog watchdog;

	public TabHolder(final Tab tab, final TextArea console, final ExecuteWatchdog watchdog) {
		this.tab = tab;
		this.console = console;
		this.watchdog = watchdog;
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
}
