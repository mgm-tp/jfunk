package com.mgmtp.jfunk.application.runner;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mgmtp.jfunk.application.MultimapSerializer;
import javafx.geometry.Rectangle2D;

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
//		cfg.getTestProperties().put("WebDriver", "chrome");
//		cfg.getTestProperties().put("WebDriver", "ie");
//		cfg.getTestProperties().put("WebDriver", "firefox");
//		cfg.getTestProperties().put("Test System", "dev");
//		cfg.getTestProperties().put("Test System", "prod");
		Gson g = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Multimap.class, MultimapSerializer.INSTANCE).create();
		System.out.println(g.toJson(cfg));
	}
}
