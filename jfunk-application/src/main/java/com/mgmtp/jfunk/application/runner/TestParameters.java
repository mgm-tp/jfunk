package com.mgmtp.jfunk.application.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class TestParameters {
	private String jFunkProps;
	private final Map<String, String> testProps = new HashMap<>(2);
	private final List<String> commandLineArgs = new ArrayList<>(2);

	public String getjFunkProps() {
		return jFunkProps;
	}

	public void setjFunkProps(final String jFunkProps) {
		this.jFunkProps = jFunkProps;
	}

	public Map<String, String> getTestProps() {
		return testProps;
	}

	public List<String> getCommandLineArgs() {
		return commandLineArgs;
	}

	public void putTestProp(final String key, final String value) {
		testProps.put(key, value);
	}

	public void addCommandLineArg(final String arg) {
		commandLineArgs.add(arg);
	}
}
