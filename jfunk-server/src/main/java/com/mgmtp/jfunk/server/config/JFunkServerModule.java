/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.server.config;

import static org.apache.commons.lang3.StringUtils.leftPad;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.mgmtp.jfunk.core.JFunkBase;
import com.mgmtp.jfunk.core.config.JFunkBaseModule;
import com.mgmtp.jfunk.server.JFunkServer;
import com.mgmtp.jfunk.server.resources.ScriptsResource;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Guice module for the jFunk Server.
 * 
 * @author rnaegele
 */
public class JFunkServerModule extends AbstractModule {

	private final Logger log = Logger.getLogger(getClass());

	private final int threadCount;
	private final int port;

	public JFunkServerModule(final int threadCount, final int port) {
		this.threadCount = threadCount;
		this.port = port;
	}

	@Override
	protected void configure() {
		install(new JFunkBaseModule());

		// Bind the resource, so Jersey knows about it.
		bind(ScriptsResource.class);

		// Install servlet module setting a the Jersey/Guice integration.
		install(new ServletModule() {
			@Override
			protected void configureServlets() {
				serve("/*").with(GuiceContainer.class,
						ImmutableMap.of(JSONConfiguration.FEATURE_POJO_MAPPING, "true",
								ResourceConfig.FEATURE_TRACE, "true",
								ResourceConfig.FEATURE_TRACE_PER_REQUEST, "true"));
			}
		});

		bind(JFunkBase.class).to(JFunkServer.class);
	}

	/**
	 * Sets up a binding for an embedded Jetty.
	 */
	@Singleton
	@Provides
	Server provideServer(final Injector injector, final ExecutorService execService) {
		final Server server = new Server(port);
		ServletContextHandler sch = new ServletContextHandler(server, "/jfunk");
		sch.addEventListener(new JFunkServletContextListener(injector));
		sch.addFilter(GuiceFilter.class, "/*", null);
		sch.addServlet(DefaultServlet.class, "/");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					execService.shutdown();
					execService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					log.error(ex.getMessage(), ex);
				}
				try {
					server.stop();
				} catch (Exception ex) {
					log.error("Error stopping Jetty.", ex);
				}
			}
		});
		return server;
	}

	/**
	 * Sets up an executor service for running jFunk scripts.
	 */
	@Singleton
	@Provides
	ExecutorService provideExecutorService() {
		ThreadFactory threadFactory = new ThreadFactory() {
			private final AtomicInteger threadNumber = new AtomicInteger(1);

			@Override
			public Thread newThread(final Runnable r) {
				// Two-digit counter, used in load test.
				int id = threadNumber.getAndIncrement();
				String threadName = leftPad(String.valueOf(id), 2, "0");
				Thread th = new Thread(r);
				th.setName(threadName);
				th.setDaemon(false);
				return th;
			}
		};
		return threadCount >= 0
				? Executors.newFixedThreadPool(threadCount, threadFactory)
				: Executors.newCachedThreadPool(threadFactory);
	}
}