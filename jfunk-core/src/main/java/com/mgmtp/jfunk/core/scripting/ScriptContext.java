/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static com.mgmtp.jfunk.common.util.Varargs.va;
import groovy.lang.Closure;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.JFileChooser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.google.inject.Injector;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.config.StackedScope;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.random.RandomCollection;
import com.mgmtp.jfunk.common.random.Randomizable;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.data.State;
import com.mgmtp.jfunk.core.event.AfterCommandEvent;
import com.mgmtp.jfunk.core.event.AfterModuleEvent;
import com.mgmtp.jfunk.core.event.BeforeCommandEvent;
import com.mgmtp.jfunk.core.event.BeforeModuleEvent;
import com.mgmtp.jfunk.core.event.ModuleInitializedEvent;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.reporting.Reporter;
import com.mgmtp.jfunk.core.util.ConfigLoader;
import com.mgmtp.jfunk.core.util.CsvDataProcessor;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * <p>
 * Provides the whole logic around scripting in jFunk. Groovy script commands delegate to this
 * class.
 * </p>
 * <p>
 * Delegate methods for script commands are annotated with {@link Cmd}. This will automatically
 * trigger {@link BeforeCommandEvent}s and {@link AfterCommandEvent}s around these methods by a
 * Guice AOP method interceptor.
 * </p>
 * 
 * @author rnaegele
 * @version $Id$
 */
@ScriptScoped
@NotThreadSafe
public class ScriptContext {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Set<Reporter> reporters = newHashSetWithExpectedSize(2);
	private final List<Throwable> errors = newArrayListWithExpectedSize(1);
	private final Provider<DataSource> dataSourceProvider;
	private final Configuration config;
	private final MathRandom random;
	private final EventBus eventBus;
	private final Injector injector;
	private final Provider<ModuleBuilder> moduleBuilderProvider;
	private final StackedScope moduleScope;
	private final CsvDataProcessor csvDataProcessor;
	private final Charset defaultCharset;

	private File script;

	@Inject
	ScriptContext(final Provider<DataSource> dataSourceProvider, final Configuration config, final MathRandom random, final EventBus eventBus,
			final Injector injector, final Provider<ModuleBuilder> moduleBuilderProvider, final StackedScope moduleScope,
			final CsvDataProcessor csvDataProcessor, final Charset charset) {
		this.dataSourceProvider = dataSourceProvider;
		this.config = config;
		this.random = random;
		this.eventBus = eventBus;
		this.injector = injector;
		this.moduleBuilderProvider = moduleBuilderProvider;
		this.moduleScope = moduleScope;
		this.csvDataProcessor = csvDataProcessor;
		this.defaultCharset = charset;
	}

	/**
	 * Sets the script file.
	 * 
	 * @param script
	 *            the script to set
	 */
	public void setScript(final File script) {
		this.script = script;
	}

	/**
	 * Gets the script directory.
	 * 
	 * @return the script file
	 */
	public String getScriptDir() {
		return script != null ? script.getParentFile().getPath() : null;
	}

	/**
	 * Gets the script file.
	 * 
	 * @return the script file
	 */
	public File getScriptFile() {
		return script;
	}

	/**
	 * @return the reporters
	 */
	public Set<Reporter> getReporters() {
		return reporters;
	}

	/**
	 * Registers a reporter.
	 * 
	 * @param reporter
	 *            the reporter
	 */
	@Cmd
	public Reporter registerReporter(final Reporter reporter) {
		injector.injectMembers(reporter);
		reporters.add(reporter);
		return reporter;
	}

	/**
	 * Opens a file chooser dialog which can then be used to choose a file or directory and assign
	 * the path of the chosen object to a variable. The name of the variable must be passed as a
	 * parameter.
	 * 
	 * @param fileKey
	 *            the key the selected file path is stored under in the configuration
	 * @return the chosen file
	 */
	@Cmd
	public File chooseFile(final String fileKey) {
		log.debug("Opening file chooser dialog");
		JFileChooser fileChooser = new JFileChooser(System.getProperty(JFunkConstants.USER_DIR));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setPreferredSize(new Dimension(600, 326));
		int fileChooserResult = fileChooser.showOpenDialog(null);

		if (fileChooserResult == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String filename = file.toString();
			log.info("Assigning file path '{}' to property '{}'", filename, fileKey);
			config.put(fileKey, filename);
			return file;
		}

		log.error("No file or directory was chosen, execution will abort");
		throw new IllegalArgumentException("No file or directory was chosen");
	}

	/**
	 * Randomly selects an item from a list of Strings. List items may contain placeholder tokens.
	 * the result is stored in the configuration under the specified key and also return by this
	 * method.
	 * 
	 * @param propertyKey
	 *            the property key under which to store the result
	 * @param randomValues
	 *            the list of String
	 * @return a randomly chosen String from the list
	 */
	@Cmd
	public String chooseRandom(final String propertyKey, final List<String> randomValues) {
		Randomizable<String> choice = new RandomCollection<String>(random, randomValues);
		String currentValue = choice.get();
		if (log.isDebugEnabled()) {
			log.debug("Chosen value: " + currentValue);
		}
		currentValue = resolveProperty(currentValue);
		config.put(propertyKey, currentValue);
		if (log.isDebugEnabled()) {
			log.debug("... value for '{}' was set to {}", propertyKey, currentValue);
		}
		return currentValue;
	}

	/**
	 * Copies a {@link DataSet} entry from one {@link DataSet} to another.
	 * 
	 * @param srcDataSetKey
	 *            the source data set key
	 * @param srcKey
	 *            the source entry key
	 * @param destDataSetKey
	 *            the destination data set key
	 * @param destKey
	 *            the destination entry key
	 */
	@Cmd
	public void copyFormEntry(final String srcDataSetKey, final String srcKey, final String destDataSetKey, final String destKey) {
		Map<String, DataSet> currentDataSets = dataSourceProvider.get().getCurrentDataSets();
		DataSet srcDataSet = currentDataSets.get(srcDataSetKey);
		DataSet destDataSet = currentDataSets.get(destDataSetKey);
		destDataSet.setFixedValue(destKey, srcDataSet.getValue(srcKey));
	}

	/**
	 * Executes a {@link Closure} instance representing a block in a Groovy script and expects one
	 * of the specified exceptions to be thrown. If no exception is thrown, an
	 * {@link IllegalStateException} is thrown, any unexpected exception is re-thrown. If the list
	 * of excepted exceptions is empty, any thrown exception is considered a success.
	 * 
	 * @param exceptionsList
	 *            the list of exception classes
	 * @param closure
	 *            the {@link Closure} representing a Groovy block
	 */
	@Cmd
	public void exceptional(final List<Class<? extends Exception>> exceptionsList, final Closure<Void> closure) {
		boolean noExceptionOccurred = false;

		try {
			closure.call();
			noExceptionOccurred = true;
		} catch (final RuntimeException ex) {
			if (exceptionsList.isEmpty()) {
				log.info("Exception " + ex.getClass().getName() + " was thrown but not expected in exceptional block.", ex);
			} else {
				for (Class<? extends Exception> exceptionClass : exceptionsList) {
					Throwable th = ex;
					while (th != null) {
						if (th.getClass() == exceptionClass) {
							log.info("Expected Exception '{}' was thrown", exceptionClass.getName());
							return;
						}
						th = th.getCause();
					}
					log.info("Expected Exception '{}' was not thrown", exceptionClass.getName());
				}
				log.error("None of the expected exceptions were thrown.", ex);
				throw ex;
			}
		}

		if (noExceptionOccurred) {
			throw new IllegalStateException("No exception occurred within exceptional block.");
		}
	}

	/**
	 * Prepares the next {@link DataSet} with the specified key for use. Depending on the type of
	 * {@link DataSource}, this may trigger data generation.
	 * 
	 * @param dataSetKey
	 *            the key of the {@link DataSet}
	 */
	@Cmd
	public void generate(final String dataSetKey) {
		checkArgument(dataSetKey != null, "Global generate is not allowed. Please specify a data set key.");
		log.info("Generating new data for dataSetKey={}", dataSetKey);
		dataSourceProvider.get().getNextDataSet(resolveProperty(dataSetKey));
	}

	/**
	 * Gets the value for the specified key from the configuration resolving any placeholders tokens
	 * within.
	 * 
	 * @param configKey
	 *            the config key
	 * @return the config value
	 */
	@Cmd
	public String get(final String configKey) {
		return resolveProperty(configKey);
	}

	/**
	 * Loads the properties file for the test. This file may reference further configuration files,
	 * which are searched relatively to the configuration directory. These extra files must have
	 * keys starting with {@code system.properties.}.
	 * 
	 * @param fileName
	 *            the name of the properties file, relative to jFunk's configuration directory
	 * @param preserveExistingProps
	 *            if {@code true}, already existing properties are preserved
	 */
	@Cmd
	public void load(final String fileName, final boolean preserveExistingProps) {
		ConfigLoader.loadConfig(config, fileName, preserveExistingProps, false);
		String scriptDir = getScriptDir();
		if (scriptDir != null) {
			config.put(JFunkConstants.SCRIPT_DIR, scriptDir);
			config.put(JFunkConstants.SCRIPT_NAME, script.getName());
		}
		config.put(JFunkConstants.THREAD_ID, Thread.currentThread().getName());
	}

	/**
	 * Logs the specified message at info level.
	 * 
	 * @param message
	 *            the log message
	 */
	@Cmd
	public void log(final String message) {
		log.info(resolveProperty(message));
	}

	/**
	 * Creates and runs a dynamic script module.
	 * 
	 * @param moduleName
	 *            the name of the module
	 * @param attributes
	 *            a map of module attributes; the only attribute that is evaluated is "dataSetKey"
	 * @param closure
	 *            the Groovy closure containing the logic to be executed within the script module
	 */
	@Cmd
	public void module(final String moduleName, final Map<?, ?> attributes, final Closure<Void> closure) {
		moduleBuilderProvider.get().invokeMethod("module", newArrayList(moduleName, attributes, closure));
	}

	/**
	 * Executes the specified closure if at least one exception had previously been recorded in the
	 * currently executing script.
	 * 
	 * @param closure
	 *            the {@link Closure} representing a Groovy block
	 */
	@Cmd
	public void onError(final Closure<Void> closure) {
		int errorCount = errors.size();
		if (errorCount > 0) {
			log.info(errorCount + " error" + (errorCount == 1 ? "" : "s") + " in list --> executing block");
			closure.call();
			log.info("Finished OnError block");
		} else {
			log.info("No errors in list --> skipping block");
		}
	}

	/**
	 * Runs the specified closure catching any exception that might occur during execution.
	 * 
	 * @param closure
	 *            the {@link Closure} representing a Groovy block
	 */
	@Cmd
	public void optional(final Closure<Void> closure) {
		log.info("Executing optional block ...");
		try {
			closure.call();
		} catch (final Exception ex) {
			log.error("Exception executing optional block: {}", ex.getMessage());
			errors.add(ex);
		} catch (AssertionError err) {
			log.error("Assertion failed executing optional block: {}", err.getMessage());
			errors.add(err);
		}
		log.info("... finished execution of optional block");
	}

	/**
	 * Processes a CSV file.
	 * 
	 * @param csvFile
	 *            the file
	 * @param delimiter
	 *            the
	 * @param quoteChar
	 *            the quote character ('\0' for no quoting)
	 * @param charset
	 *            the character set
	 * @param closure
	 *            the {@link Closure} representing a Groovy block
	 */
	@Cmd
	public void processCsvFile(final String csvFile, final String delimiter, final char quoteChar, final Charset charset, final Closure<Void> closure) {
		File f = new File(csvFile);
		try {
			config.extractFromArchive(f, true);
			checkState(f.exists(), "CSV file not found: " + f);
			Reader reader = Files.newReader(f, charset == null ? defaultCharset : charset);
			csvDataProcessor.processFile(reader, delimiter, quoteChar, closure);
		} catch (IOException ex) {
			throw new IllegalStateException("Error reading CSV file: " + f, ex);
		}
	}

	/**
	 * Prompts for closure-line input. the input is stored in the configuration under the specified
	 * key.
	 * 
	 * @param configKey
	 *            the key for storing the input in the configuration
	 * @param message
	 *            the prompt message
	 * @return the input
	 */
	@Cmd
	public String prompt(final String configKey, final String message) {
		System.out.print(resolveProperty(message) + " "); //NOSONAR
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String value = StringUtils.trim(in.readLine());
			config.put(configKey, value);
			return value;
			// Stream nicht schliessen
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Clears the internal errors list.
	 */
	@Cmd
	public void resetError() {
		log.info("Clearing errors");
		errors.clear();
	}

	/**
	 * Resets fixed values in the data source for the {@link DataSet} with the specified key and the
	 * entry with the specified key.
	 * 
	 * @param dataSetKey
	 *            if {@code null}, all fixed values are reset
	 * @param entryKey
	 *            if {@code null}, all fixed values in the {@link DataSet} are reset
	 */
	@Cmd
	public void resetFixedData(final String dataSetKey, final String entryKey) {
		if (dataSetKey == null) {
			log.info("Resetting all fixed values.");
			dataSourceProvider.get().resetFixedValues();
			return;
		}

		String resolvedDataSetKey = resolveProperty(dataSetKey);
		if (entryKey == null) {
			log.info("Resetting fixed values for data set '{}'", resolvedDataSetKey);
			dataSourceProvider.get().resetFixedValues(resolvedDataSetKey);
			return;
		}

		String resolvedEntryKey = resolveProperty(entryKey);
		log.info("Resetting fixed value for data set '{}' and entry '{}'", resolvedDataSetKey, resolvedEntryKey);
		dataSourceProvider.get().resetFixedValue(resolvedDataSetKey, resolvedEntryKey);
	}

	/**
	 * Instantiates and runs a {@link TestModule}.
	 * 
	 * @param testModuleClassName
	 *            the fully qualified class name of the {@link TestModule}
	 */
	@Cmd
	public void run(final String testModuleClassName) {
		doRun(createModuleInstance(testModuleClassName));
	}

	/**
	 * Runs the specified {@link TestModule}.
	 * 
	 * @param testModule
	 *            the test module
	 */
	@Cmd
	public void run(final TestModule testModule) {
		doRun(testModule);
	}

	void doRun(final TestModule testModule) {
		Throwable th = null;
		TestModule moduleToRun = testModule;

		try {
			moduleScope.enterScope();

			// perform DI on test module instance
			injector.injectMembers(moduleToRun);

			eventBus.post(new ModuleInitializedEvent(moduleToRun));
			eventBus.post(new InternalBeforeModuleEvent(moduleToRun));
			eventBus.post(new BeforeModuleEvent(moduleToRun));
			moduleToRun.execute();
		} catch (RuntimeException ex) {
			th = ex;

			// We need to log the exception here on module level,
			// so it makes it into the log file in the module's archive
			log.error("Exception executing module: " + moduleToRun.getName(), ex);

			// Wrap into ModuleExecutionException, so we know later that
			// we don't have to log it again.
			throw new ModuleExecutionException(moduleToRun, ex);
		} catch (AssertionError err) {
			th = err;

			// We need to log the exception here on module level,
			// so it makes it into the log file in the module's archive
			log.error("Assertion failed in module: " + moduleToRun.getName(), err);

			// Wrap into ModuleExecutionException, so we know later that
			// we don't have to log it again.
			throw new ModuleExecutionException(moduleToRun, err);
		} finally {
			try {
				moduleToRun.setError(th != null);
				eventBus.post(new AfterModuleEvent(moduleToRun, th));
				eventBus.post(new InternalAfterModuleEvent(moduleToRun, th));
			} finally {
				moduleScope.exitScope();
			}
		}
	}

	private TestModule createModuleInstance(final String moduleClassName) {
		try {
			Class<? extends TestModule> moduleClass = Class.forName(moduleClassName).asSubclass(TestModule.class);
			Constructor<? extends TestModule> constructor = null;
			try {
				constructor = moduleClass.getConstructor(State.class);
				return constructor.newInstance((State) null);
			} catch (NoSuchMethodException ex) {
				constructor = moduleClass.getConstructor();
				return constructor.newInstance();
			}
		} catch (Exception ex) {
			throw new JFunkException("Error instantiating module from config: " + moduleClassName, ex);
		}
	}

	@Cmd
	public void resetDataSource() {
		dataSourceProvider.get().reset();
	}

	/**
	 * Sets a {@link DataSet} entry. In order to affect data generation, this method must be called
	 * before calling {@link #generate(String)}.
	 * 
	 * @param dataSetKey
	 *            the {@link DataSet} key
	 * @param entryKey
	 *            the key of the entry in the specified {@link DataSet}
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void setFormEntry(final String dataSetKey, final String entryKey, final String value) {
		doSetFormEntry(dataSetKey, entryKey, value);
	}

	/**
	 * Sets a {@link DataSet} entry. In order to affect data generation, this method must be called
	 * before calling {@link #generate(String)}.
	 * 
	 * @param dataSetKey
	 *            the {@link DataSet} key
	 * @param entryKey
	 *            the key of the entry in the specified {@link DataSet}
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void setFormEntry(final String dataSetKey, final String entryKey, final boolean value) {
		doSetFormEntry(dataSetKey, entryKey, String.valueOf(value));
	}

	/**
	 * Sets a {@link DataSet} entry. In order to affect data generation, this method must be called
	 * before calling {@link #generate(String)}.
	 * 
	 * @param dataSetKey
	 *            the {@link DataSet} key
	 * @param entryKey
	 *            the key of the entry in the specified {@link DataSet}
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void setFormEntry(final String dataSetKey, final String entryKey, final int value) {
		doSetFormEntry(dataSetKey, entryKey, String.valueOf(value));
	}

	/**
	 * Sets a {@link DataSet} entry. In order to affect data generation, this method must be called
	 * before calling {@link #generate(String)}.
	 * 
	 * @param dataSetKey
	 *            the {@link DataSet} key
	 * @param entryKey
	 *            the key of the entry in the specified {@link DataSet}
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void setFormEntry(final String dataSetKey, final String entryKey, final long value) {
		doSetFormEntry(dataSetKey, entryKey, String.valueOf(value));
	}

	void doSetFormEntry(final String dataSetKey, final String entryKey, final String value) {
		String resolvedDataSetKey = resolveProperty(dataSetKey);
		String resolvedEntryKey = resolveProperty(entryKey);
		String resolvedValue = resolveProperty(value);

		log.debug("Setting entry '{}' in data set '{}' to fixed value '{}'", va(resolvedEntryKey, resolvedDataSetKey, resolvedValue));
		dataSourceProvider.get().setFixedValue(resolvedDataSetKey, resolvedEntryKey, resolvedValue);
	}

	/**
	 * Sets a property to a given value. Both key and value can contain properties. The following
	 * rules apply:
	 * <ul>
	 * <li>the key will be evaluated on execution of the closure</li>
	 * <li>the value will be evaluated on querying the property</li>
	 * <li>if the value should also be evaluated on execution you need to use
	 * {@link #setNow(String, String)}</li>
	 * </ul>
	 * 
	 * @param key
	 *            the config key
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void set(final String key, final String value) {
		doSet(key, value);
	}

	/**
	 * Sets a property to a given value. Both key and value can contain properties. The following
	 * rules apply:
	 * <ul>
	 * <li>the key will be evaluated on execution of the closure</li>
	 * <li>the value will be evaluated on querying the property</li>
	 * <li>if the value should also be evaluated on execution you need to use
	 * {@link #setNow(String, String)}</li>
	 * </ul>
	 * 
	 * @param key
	 *            the config key
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void set(final String key, final int value) {
		doSet(key, String.valueOf(value));
	}

	/**
	 * Sets a property to a given value. Both key and value can contain properties. The following
	 * rules apply:
	 * <ul>
	 * <li>the key will be evaluated on execution of the closure</li>
	 * <li>the value will be evaluated on querying the property</li>
	 * <li>if the value should also be evaluated on execution you need to use
	 * {@link #setNow(String, String)}</li>
	 * </ul>
	 * 
	 * @param key
	 *            the config key
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void set(final String key, final boolean value) {
		doSet(key, String.valueOf(value));
	}

	/**
	 * Sets a property to a given value. Both key and value can contain properties. The following
	 * rules apply:
	 * <ul>
	 * <li>the key will be evaluated on execution of the closure</li>
	 * <li>the value will be evaluated on querying the property</li>
	 * <li>if the value should also be evaluated on execution you need to use
	 * {@link #setNow(String, String)}</li>
	 * </ul>
	 * 
	 * @param key
	 *            the config key
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void set(final String key, final long value) {
		doSet(key, String.valueOf(value));
	}

	/**
	 * Sets a property to a given value. Both key and value can contain properties. The following
	 * rules apply:
	 * <ul>
	 * <li>the key will be evaluated on execution of the closure</li>
	 * <li>the value will be evaluated on execution of the closure</li>
	 * <li>if the value should be evaluated on querying the property you need to use
	 * {@link #set(String, String)}</li>
	 * </ul>
	 * 
	 * @param key
	 *            the config key
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void setNow(final String key, final String value) {
		String resolvedValue = resolveProperty(value);
		doSet(key, resolvedValue);
	}

	/**
	 * Sets a property to a given value. Both key and value can contain properties. The following
	 * rules apply:
	 * <ul>
	 * <li>the key will be evaluated on execution of the closure</li>
	 * <li>the value will be evaluated on execution of the closure</li>
	 * <li>if the value should be evaluated on querying the property you need to use
	 * {@link #set(String, String)}</li>
	 * </ul>
	 * 
	 * @param key
	 *            the config key
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void setNow(final String key, final long value) {
		String resolvedValue = resolveProperty(String.valueOf(value));
		doSet(key, resolvedValue);
	}

	/**
	 * Sets a property to a given value. Both key and value can contain properties. The following
	 * rules apply:
	 * <ul>
	 * <li>the key will be evaluated on execution of the closure</li>
	 * <li>the value will be evaluated on execution of the closure</li>
	 * <li>if the value should be evaluated on querying the property you need to use
	 * {@link #set(String, String)}</li>
	 * </ul>
	 * 
	 * @param key
	 *            the config key
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void setNow(final String key, final int value) {
		String resolvedValue = resolveProperty(String.valueOf(value));
		doSet(key, resolvedValue);
	}

	/**
	 * Sets a property to a given value. Both key and value can contain properties. The following
	 * rules apply:
	 * <ul>
	 * <li>the key will be evaluated on execution of the closure</li>
	 * <li>the value will be evaluated on execution of the closure</li>
	 * <li>if the value should be evaluated on querying the property you need to use
	 * {@link #set(String, String)}</li>
	 * </ul>
	 * 
	 * @param key
	 *            the config key
	 * @param value
	 *            the value to set
	 */
	@Cmd
	public void setNow(final String key, final boolean value) {
		String resolvedValue = resolveProperty(String.valueOf(value));
		doSet(key, resolvedValue);
	}

	void doSet(final String key, final String value) {
		String resolvedKey = resolveProperty(key);
		if (value == null) {
			log.info("Removing property " + resolvedKey);
			config.remove(resolvedKey);
		} else {
			log.info("Setting property " + resolvedKey + " = " + value);
			config.put(resolvedKey, resolveProperty(value));
		}
	}

	/**
	 * Sets a {@link DataSet} entry from a configuration property.
	 * 
	 * @param configProperty
	 *            the configuration key
	 * @param dataSetKey
	 *            the {@link DataSet} key
	 * @param entryKey
	 *            the key of the {@link DataSet} entry
	 */
	@Cmd
	public void setToFormEntry(final String configProperty, final String dataSetKey, final String entryKey) {
		String resolvedConfigProperty = resolveProperty(configProperty);
		String resolvedDataSetKey = resolveProperty(dataSetKey);
		String resolvedEntryKey = resolveProperty(entryKey);

		DataSet dataSet = dataSourceProvider.get().getCurrentDataSet(resolvedDataSetKey);
		if (dataSet == null) {
			throw new IllegalStateException("DataSet " + resolvedDataSetKey + " was null.");
		}
		String value = dataSet.getValue(resolvedEntryKey);

		log.info("Setting property '{}' to '{}'", resolvedConfigProperty, value);
		config.put(resolvedConfigProperty, value);
	}

	private String resolveProperty(final String configProperty) {
		return config.processPropertyValue(configProperty);
	}
}
