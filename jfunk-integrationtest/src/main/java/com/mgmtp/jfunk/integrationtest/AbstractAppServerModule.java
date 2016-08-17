/*
 * Copyright (c) 2016 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mgmtp.jfunk.integrationtest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import com.mgmtp.jfunk.core.module.TestModuleImpl;

/**
 * @author sstrohmaier
 */
public abstract class AbstractAppServerModule extends TestModuleImpl {

	public static final String GRIZZLY_SERVER_BASE_URL = "http://localhost:8080/";

	private static String testHtmlFilePath;
	private static String testApplicationPath;
	private static HttpServer server;

	public AbstractAppServerModule(String testApplicationPath, String testHtmlFilePath) {
		super(null);
		AbstractAppServerModule.testApplicationPath = testApplicationPath;
		AbstractAppServerModule.testHtmlFilePath = testHtmlFilePath;
	}

	@Override
	protected void executeSteps() {
		startupServer();
		try {
			executeInnerSteps();
		} catch (Exception e) {
			shutdownServer();
			throw (e);
		}
		shutdownServer();
	}

	public abstract void executeInnerSteps();

	protected static void startupServer() {
		server = HttpServer.createSimpleServer();
		server.getServerConfiguration().addHttpHandler(new HttpHandler() {
			public void service(Request request, Response response) throws Exception {
				String htmlFileContent = null;
				try {
					htmlFileContent = FileUtils.readFileToString(new File("static/" + testHtmlFilePath), "UTF-8");
					response.setContentType("text/html");
					response.setContentLength(htmlFileContent.length());
					response.getWriter().write(htmlFileContent);
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}, "/" + testApplicationPath);
		try {
			server.start();
			System.err.println("Server was started up.");
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	protected static void shutdownServer() {
		server.shutdownNow();
		server = null;
		System.err.println("Server was shutdown.");
	}
}
