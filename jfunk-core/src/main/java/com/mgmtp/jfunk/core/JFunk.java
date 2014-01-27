/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
package com.mgmtp.jfunk.core;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.min;

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.FixedSizeThreadExecutor;
import com.mgmtp.jfunk.core.config.JFunkDefaultModule;
import com.mgmtp.jfunk.core.config.ModulesLoader;
import com.mgmtp.jfunk.core.event.BeforeRunEvent;
import com.mgmtp.jfunk.core.exception.JFunkExecutionException;
import com.mgmtp.jfunk.core.scripting.ScriptExecutor;

/**
 * Class for running a jFunk test. See {@link #main(String[])} for a description of all command line
 * arguments.
 * 
 */
public final class JFunk extends JFunkBase {

	private final List<File> scripts;

	private int threadCount;
	private final Properties scriptProperties;
	private final ScriptExecutor scriptExecutor;
	private final String name;

	/**
	 * Creates a JFunk instance.
	 * 
	 * @param scriptExecutor
	 *            Used to execute the script.
	 * @param threadCount
	 *            Number of threads to be used. Allows for parallel execution of test scripts.
	 * @param parallel
	 *            Allows a single script to be executed in parallel depending on the number of
	 *            threads specified. The argument is ignored if multiple scripts are specified.
	 * @param scripts
	 *            A list of test scripts. Must contain at least one script.
	 * @param scriptProperties
	 *            script properties passed in with {@code -S<key>=<value>}
	 */
	@Inject
	public JFunk(final ScriptExecutor scriptExecutor, final EventBus eventBus, @Assisted final int threadCount,
			@Assisted final boolean parallel, @Assisted final List<File> scripts, @Assisted final Properties scriptProperties) {
		super(eventBus);

		this.scriptExecutor = scriptExecutor;
		this.threadCount = threadCount;
		this.scriptProperties = scriptProperties;

		List<File> tmpScripts = Lists.newArrayList();

		boolean dirChosen = false;
		boolean fileChosen = false;

		Set<String> dirNames = newHashSet();

		for (File file : scripts) {
			if (file.isDirectory()) {
				if (fileChosen) {
					throw new IllegalArgumentException("Directories and files as arguments cannot be mixed");
				}
				dirNames.add(file.getName());

				File[] scriptFiles = file.listFiles(new FileFilter() {
					@Override
					public boolean accept(final File f) {
						return !f.isDirectory() && f.getName().endsWith("groovy");
					}
				});
				if (scriptFiles.length == 0) {
					throw new IllegalArgumentException("Directory " + file + " does not contain any jFunk script files");
				}
				tmpScripts.addAll(Arrays.asList(scriptFiles));
				dirChosen = true;
			} else {
				if (dirChosen) {
					throw new IllegalArgumentException("Directories and files as arguments cannot be mixed");
				}

				dirNames.add(file.getParentFile().getName());
				tmpScripts.add(file);
				fileChosen = true;
			}
		}

		name = on(',').join(dirNames);

		if (parallel) {
			if (tmpScripts.size() > 1) {
				LOG.warn("The '-parallel' argument is ignored because multiple scripts were specified.");
			} else {
				// Add the single script to the list, so we have it in there once per thread.
				File script = tmpScripts.get(0);
				for (int i = 0; i < threadCount - 1; ++i) {
					tmpScripts.add(script);
				}
			}
		}

		this.scripts = tmpScripts;

		for (File file : this.scripts) {
			RESULT_LOG.info("Adding " + file.getName() + " to processing queue");
		}
	}

	@Override
	protected BeforeRunEvent createBeforeRunEvent() {
		BeforeRunEvent event = super.createBeforeRunEvent();
		event.addParameter("threadCount", threadCount);
		event.addParameter("name", name);
		return event;
	}

	/**
	 * Executes the jFunk test. A thread pool ({@link ExecutorService}) is created with the number
	 * of configured threads, which handles concurrent script execution.
	 */
	@Override
	protected void doExecute() throws Exception {
		ExecutorService execService = createExecutorService();
		CompletionService<Boolean> completionService = new ExecutorCompletionService<>(execService);

		for (final File script : scripts) {
			completionService.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					boolean success = false;
					StopWatch stopWatch = new StopWatch();
					stopWatch.start();

					RESULT_LOG.info("Thread " + Thread.currentThread().getName() + ": starting execution of script "
							+ script.getName());

					try {
						success = scriptExecutor.executeScript(script, scriptProperties);
					} catch (Throwable th) {
						LOG.error(th.getMessage(), th);
					} finally {

						LOG.info("SCRIPT EXECUTION " + (success ? "SUCCESSFUL" : "FAILED") + " (" + script + ")");

						RESULT_LOG.info("Thread " + Thread.currentThread().getName() + ": finished execution of script "
								+ script.getName() + " (took "
								+ stopWatch + " H:mm:ss.SSS)");
					}
					return success;
				}
			});
		}

		boolean overallResult = true;
		for (int i = 0, size = scripts.size(); i < size; ++i) {
			if (!completionService.take().get()) {
				overallResult = false;
			}
		}

		shutDownExecutorService(execService);

		if (!overallResult) {
			throw new JFunkExecutionException();
		}
	}

	private ExecutorService createExecutorService() {
		return new FixedSizeThreadExecutor(min(threadCount, scripts.size()), new ThreadFactory() {
			private final AtomicInteger threadNumber = new AtomicInteger(1);

			@Override
			public Thread newThread(final Runnable r) {
				int id = threadNumber.getAndIncrement();
				String threadName = StringUtils.leftPad(String.valueOf(id), 2, "0");
				Thread th = new Thread(r);
				th.setName(threadName);
				return th;
			}
		});
	}

	private void shutDownExecutorService(final ExecutorService execService) {
		try {
			execService.shutdownNow();
			execService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException ex) {
			LOG.error("Script execution was interrupted.", ex);
		}
	}

	/**
	 * Starts jFunk.
	 * 
	 * <pre>
	 * -threadcount=&lt;count&gt;    Optional    Number of threads to be used. Allows for parallel
	 *                                     execution of test scripts.
	 * -parallel               Optional    Allows a single script to be executed in parallel
	 *                                     depending on the number of threads specified. The
	 *                                     argument is ignored if multiple scripts are specified.
	 * &lt;script parameters&gt     Optional    Similar to Java system properties they can be provided
	 *                                     as key-value-pairs preceded by -S, e.g. -Skey=value.
	 *                                     These parameters are then available in the script as
	 *                                     Groovy variables.
	 * &lt;script(s)&gt;             Required    At least one test script must be specified.
	 *
	 * Example:
	 * java -cp &lt;jFunkClasspath&gt; com.mgmtp.jfunk.core.JFunk -Skey=value -threadcount=4 -parallel mytest.script
	 * </pre>
	 * 
	 * @param args
	 *            The program arguments.
	 */
	public static void main(final String[] args) {
		SLF4JBridgeHandler.install();

		boolean exitWithError = true;
		StopWatch stopWatch = new StopWatch();

		try {
			RESULT_LOG.info("jFunk started");
			stopWatch.start();

			int threadCount = 1;
			boolean parallel = false;
			Properties scriptProperties = new Properties();
			List<File> scripts = Lists.newArrayList();

			for (String arg : args) {
				if (arg.startsWith("-threadcount")) {
					String[] split = arg.split("=");
					Preconditions.checkArgument(split.length == 2,
							"The number of threads must be specified as follows: -threadcount=<value>");
					threadCount = Integer.parseInt(split[1]);
					RESULT_LOG.info("Using " + threadCount + (threadCount == 1 ? " thread" : " threads"));
				} else if (arg.startsWith("-S")) {
					arg = arg.substring(2);
					String[] split = arg.split("=");
					Preconditions
							.checkArgument(split.length == 2, "Script parameters must be given in the form -S<name>=<value>");
					scriptProperties.setProperty(split[0], normalizeScriptParameterValue(split[1]));
					RESULT_LOG.info("Using script parameter " + split[0] + " with value " + split[1]);
				} else if (arg.equals("-parallel")) {
					parallel = true;
					RESULT_LOG.info("Using parallel mode");
				} else {
					scripts.add(new File(arg));
				}
			}

			if (scripts.isEmpty()) {
				scripts.addAll(requestScriptsViaGui());

				if (scripts.isEmpty()) {
					RESULT_LOG.info("Execution finished (took " + stopWatch + " H:mm:ss.SSS)");
					System.exit(0);
				}
			}

			String propsFileName = System.getProperty("jfunk.props.file", "jfunk.properties");
			Module module = ModulesLoader.loadModulesFromProperties(new JFunkDefaultModule(), propsFileName);
			Injector injector = Guice.createInjector(module);

			JFunkFactory factory = injector.getInstance(JFunkFactory.class);
			JFunkBase jFunk = factory.create(threadCount, parallel, scripts, scriptProperties);
			jFunk.execute();

			exitWithError = false;
		} catch (JFunkExecutionException ex) {
			// no logging necessary
		} catch (Exception ex) {
			Logger.getLogger(JFunk.class).error("jFunk terminated unexpectedly.", ex);
		} finally {
			stopWatch.stop();
			RESULT_LOG.info("Execution finished (took " + stopWatch + " H:mm:ss.SSS)");
		}

		System.exit(exitWithError ? -1 : 0);
	}

	private static String normalizeScriptParameterValue(final String value) {
		if (value.startsWith("$_{") && value.endsWith("}")) {
			return value.replaceFirst("\\$_\\{", "\\${");
		}
		return value;
	}

	private static List<File> requestScriptsViaGui() {
		final List<File> scripts = new ArrayList<>();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					JFileChooser fileChooser = new JFileChooser(System.getProperty(JFunkConstants.USER_DIR));
					fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					fileChooser.setMultiSelectionEnabled(true);
					fileChooser.setPreferredSize(new Dimension(800, 450));
					int i = fileChooser.showOpenDialog(null);

					if (i == JFileChooser.APPROVE_OPTION) {
						File[] files = fileChooser.getSelectedFiles();
						scripts.addAll(Arrays.asList(files));
					}
				}
			});
		} catch (Exception e) {
			LOG.error("Error while requesting scripts via GUI", e);
		}

		return scripts;
	}
}