package com.mgmtp.jfunk.application.runner.util;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author rnaegele
 */
public class JUnitRunner {
	public static final void main(String[] args) throws ClassNotFoundException {
		checkState(args.length > 0, "Requires at least one parameter (class name)");
		Class<?> clazz = Class.forName(args[0]);
		Request request = args.length == 1 ? Request.aClass(clazz) : Request.method(clazz, args[1]);
		JUnitCore junit = new JUnitCore();
		Result result = junit.run(request);
		boolean success = result.wasSuccessful();
		System.exit(success ? 0 : 1);
	}
}
