/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.server;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.JFunk;
import com.mgmtp.jfunk.core.JFunkBase;
import com.mgmtp.jfunk.core.config.ModulesLoader;
import com.mgmtp.jfunk.core.util.ConfigLoader;
import com.mgmtp.jfunk.server.config.JFunkServerModule;

/**
 * Runs jFunk in server-mode providing a REST interface for running scripts etc.
 * 
 * @author rnaegele
 */
public class JFunkServer extends JFunkBase {

	private static final int DEFAULT_PORT = 8182;

	private final Server server;

	@Inject
	public JFunkServer(final Server server, final EventBus eventBus) {
		super(eventBus);
		this.server = server;
	}

	/**
	 * Executes the jFunk test. A thread pool ({@link ExecutorService}) is created with the number
	 * of configured threads, which handles concurrent script execution.
	 */
	@Override
	protected void doExecute() throws Exception {
		server.start();
		server.join();
	}

	/**
	 * Starts the jFunk server.
	 */
	public static void main(final String[] args) {
		SLF4JBridgeHandler.install();

		boolean exitWithError = true;

		int threadCount = -1;
		int port = DEFAULT_PORT;

		for (String arg : args) {
			if (arg.startsWith("-threadcount")) {
				String[] split = arg.split("=");
				Preconditions.checkArgument(split.length == 2, "The number of threads must be specified as follows: -threadcount=<value>");
				threadCount = Integer.parseInt(split[1]);
				RESULT_LOG.info("Using " + threadCount + (threadCount == 1 ? " thread" : " threads"));
			} else if (arg.startsWith("-port")) {
				arg = arg.substring(2);
				String[] split = arg.split("=");
				Preconditions.checkArgument(split.length == 2, "The port must be specified as follows: -port=<port>");
				port = Integer.parseInt(split[1]);
				RESULT_LOG.info("Using port " + port);
			}
		}

		if (port == DEFAULT_PORT) {
			RESULT_LOG.info("Using default port " + port);
		}
		if (threadCount == -1) {
			RESULT_LOG.info("Number of server threads not limited!");
		}

		try {

			RESULT_LOG.info("Starting Funk Server...");

			String propsFileName = System.getProperty("jfunk.props.file", "jfunk.properties");
			Module module = new JFunkServerModule(threadCount, port);
			List<Module> modules = ModulesLoader.loadModulesFromProperties(module, propsFileName);

			Injector injector = Guice.createInjector(modules);
			// load config only in order to set global properties as system properties
			// specifiying "true" as the last parameter
			ConfigLoader.loadConfig(injector.getInstance(Configuration.class), JFunkConstants.SCRIPT_PROPERTIES, false, true);

			JFunkBase jFunk = injector.getInstance(JFunkBase.class);
			jFunk.execute();

			exitWithError = false;
		} catch (Exception ex) {
			Logger.getLogger(JFunk.class).error("jFunk Server terminated unexpectedly.", ex);
		} finally {
			RESULT_LOG.info("jFunk Server stopped");
		}

		System.exit(exitWithError ? -1 : 0);
	}
}
