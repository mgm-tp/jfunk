package com.mgmtp.jfunk.application.runner;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class Dir {
	private String name;
	private Set<Dir> children = new TreeSet<>();

	public Dir(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Set<Dir> getChildren() {
		return Collections.unmodifiableSet(children);
	}

	public void addChild(final Dir child) {
		children.add(child);
	}
}
