package com.mgmtp.jfunk.server.config;

import javax.servlet.ServletContextListener;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * {@link ServletContextListener} implementation for <a
 * href="http://code.google.com/p/google-guice/wiki/Servlets">Guice Servlet</a> providing access to
 * the {@link Injector}.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class JFunkServletContextListener extends GuiceServletContextListener {

	private final Injector injector;

	public JFunkServletContextListener(final Injector injector) {
		this.injector = injector;
	}

	@Override
	protected Injector getInjector() {
		return injector;
	}
}