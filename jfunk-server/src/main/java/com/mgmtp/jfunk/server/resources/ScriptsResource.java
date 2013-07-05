/*
 * Copyright (c) 2013 mgm technology partners GmbH
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
package com.mgmtp.jfunk.server.resources;

import static java.util.Arrays.asList;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.MapMaker;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.mgmtp.jfunk.core.scripting.ScriptExecutor;
import com.mgmtp.jfunk.server.domain.ActiveScript;
import com.mgmtp.jfunk.server.domain.ActiveScript.ActiveState;
import com.mgmtp.jfunk.server.domain.FileSystemItem;
import com.mgmtp.jfunk.server.domain.FileSystemItems;
import com.mgmtp.jfunk.server.domain.ScriptParam;
import com.mgmtp.jfunk.server.domain.ScriptParams;

/**
 * Resource for Groovy scripts.
 * 
 * @author rnaegele
 */
@Singleton
@Path("scripts")
public class ScriptsResource {

	private final Logger log = Logger.getLogger(getClass());

	private final ExecutorService executorService;
	private final ScriptExecutor scriptExecutor;
	private final ConcurrentMap<UUID, ActiveScript> activeScripts = new MapMaker().makeMap();
	private final Charset charset;

	/**
	 * 
	 * @param executorService
	 *            the {@link ExecutorService} for asynchronous script execution
	 * @param scriptExecutor
	 *            used to execute scripts
	 */
	@Inject
	public ScriptsResource(final ExecutorService executorService, final ScriptExecutor scriptExecutor, final Charset charset) {
		this.executorService = executorService;
		this.scriptExecutor = scriptExecutor;
		this.charset = charset;
	}

	/**
	 * Method for downloading a Groovy script.
	 */
	@GET
	@Path("{file: .*\\.groovy}")
	@Produces(MediaType.TEXT_PLAIN)
	public String downloadScript(@PathParam("file") final File file) throws IOException {
		return Files.toString(file, charset);
	}

	/**
	 * Method for uploading a Groovy script.
	 */
	@PUT
	@Path("{file: .*\\.groovy}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response uploadScript(@PathParam("file") final File file, @Context final UriInfo uriInfo, final InputStream is)
			throws IOException {
		boolean exists = file.exists();

		FileOutputStream os = new FileOutputStream(file);
		try {
			ByteStreams.copy(is, os);
		} finally {
			closeQuietly(os);
		}

		if (exists) {
			return Response.noContent().build();
		}

		// this sets the location header for the newly created script
		return Response.created(uriInfo.getAbsolutePath()).build();
	}

	/**
	 * Method for executing a script.
	 */
	@POST
	@Path("{file: .*\\.groovy}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ActiveScript executeScript(@PathParam("file") final File file, @Context final UriInfo uriInfo,
			final ScriptParams params) {
		final Properties properties = new Properties();
		for (ScriptParam param : params.getScriptParams()) {
			properties.setProperty(param.getName(), param.getValue());
		}

		// Use a UUID as key because the same script might be run multiple times concurrently
		final UUID key = UUID.randomUUID();
		final ActiveScript script = new ActiveScript(key, file.getName(), new Date(file.lastModified()), uriInfo
				.getAbsolutePathBuilder()
				.path(file.getName()).build(), ActiveState.SCHEDULED);

		activeScripts.put(key, script);

		executorService.submit(new Runnable() {
			@Override
			public void run() {
				boolean success;

				try {
					script.setState(ActiveState.RUNNING);
					success = scriptExecutor.executeScript(file, properties);
				} catch (Exception ex) {
					success = false;
					log.error(ex.getMessage(), ex);
				} finally {
					activeScripts.remove(key);
				}

				log.info("SCRIPT EXECUTION " + (success ? "SUCCESSFUL" : "FAILED") + " (" + script + ")");
			}
		});
		return script;
	}

	/**
	 * Method for listing currently running scripts.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("running")
	public Collection<ActiveScript> getRunningScripts() {
		return Collections2.filter(activeScripts.values(), new Predicate<ActiveScript>() {
			@Override
			public boolean apply(final ActiveScript script) {
				return script.getState() == ActiveState.RUNNING;
			}
		});
	}

	/**
	 * Method for listing currently scheduled scripts.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("scheduled")
	public Collection<ActiveScript> getScheduledScripts() {
		return Collections2.filter(activeScripts.values(), new Predicate<ActiveScript>() {
			@Override
			public boolean apply(final ActiveScript script) {
				return script.getState() == ActiveState.SCHEDULED;
			}
		});
	}

	/**
	 * Method for listing currently active scripts.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("active")
	public Collection<ActiveScript> getActiveScripts() {
		return activeScripts.values();
	}

	/**
	 * Method for listing a directory.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("{path: .*(?<!\\.groovy)$}")
	public FileSystemItems list(@PathParam("path") final File path, @Context final UriInfo uriInfo) {
		FileSystemItems items = new FileSystemItems();

		List<File> files = asList(path.listFiles(new FileFilter() {
			@Override
			public boolean accept(final File f) {
				return f.isDirectory() || f.getName().endsWith(".groovy");
			}
		}));

		for (File input : files) {
			FileSystemItem fsi = new FileSystemItem(input.getName(), new Date(input.lastModified()), uriInfo
					.getAbsolutePathBuilder()
					.path(input.getName()).build());

			List<FileSystemItem> target = input.isDirectory() ? items.getDirectories() : items.getScripts();
			target.add(fsi);
		}

		return items;
	}
}
