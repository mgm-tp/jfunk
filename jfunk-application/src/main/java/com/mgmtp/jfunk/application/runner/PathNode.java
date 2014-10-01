package com.mgmtp.jfunk.application.runner;

import java.nio.file.Path;

/**
 * Created by rnaegele on 01.10.2014.
 */
public class PathNode extends Node<Path> {

	public PathNode(final Path data) {
		super(data);
	}

	@Override
	public String getDisplayString() {
		return data.getFileName().toString();
	}
}
