package com.mgmtp.jfunk.application.runner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class RunnerConfig {
	private List<String> groovyScriptDirs = new ArrayList<>();
	private LinkedHashMap<String, List<String>> testProperties = new LinkedHashMap<>();

	public List<String> getGroovyScriptDirs() {
		return groovyScriptDirs;
	}

	public Map<String, List<String>> getTestProperties() {
		return testProperties;
	}

	public static final void main(String... args) {
		RunnerConfig cfg = new RunnerConfig();
		cfg.getGroovyScriptDirs().add("scripts");
		cfg.getGroovyScriptDirs().add("groovy");
	}
}
