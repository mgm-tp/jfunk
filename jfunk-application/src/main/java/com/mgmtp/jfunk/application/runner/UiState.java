package com.mgmtp.jfunk.application.runner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class UiState {

	private double threads;
	private boolean parallel;
	private double windowX;
	private double windowY;
	private double windowWidth;
	private double windowHeight;
	private String jFunkProps;
	private Map<String, String> testProps = new HashMap<>();

	public double getThreads() {
		return threads;
	}

	public void setThreads(final double threads) {
		this.threads = threads;
	}

	public boolean isParallel() {
		return parallel;
	}

	public void setParallel(final boolean parallel) {
		this.parallel = parallel;
	}

	public double getWindowX() {
		return windowX;
	}

	public void setWindowX(final double windowX) {
		this.windowX = windowX;
	}

	public double getWindowY() {
		return windowY;
	}

	public void setWindowY(final double windoxY) {
		this.windowY = windoxY;
	}

	public double getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(final double windowWidth) {
		this.windowWidth = windowWidth;
	}

	public double getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(final double windowHeight) {
		this.windowHeight = windowHeight;
	}

	public String getjFunkProps() {
		return jFunkProps;
	}

	public void setjFunkProps(final String jFunkProps) {
		this.jFunkProps = jFunkProps;
	}

	public Map<String, String> getTestProps() {
		return testProps;
	}

	public void setTestProps(final Map<String, String> testProps) {
		this.testProps = testProps;
	}
}
