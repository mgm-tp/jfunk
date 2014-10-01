package com.mgmtp.jfunk.application.runner;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class TestClass {
	private String name;
	private Set<String> methods = new TreeSet<>();

	private TestClass(final String name, final Set<String> methods) {
		this.name = name;
		this.methods = methods;
	}

	public String getName() {
		return name;
	}

	public Set<String> getMethods() {
		return Collections.unmodifiableSet(methods);
	}

	public static class Builder {
		private String name;
		private Set<String> methods = new TreeSet<>();

		public Builder(final String name) {
			this.name = name;
		}

		public Builder addMethod(String method) {
			methods.add(method);
			return this;
		}

		public TestClass build() {
			return new TestClass(name, methods);
		}
	}
}
