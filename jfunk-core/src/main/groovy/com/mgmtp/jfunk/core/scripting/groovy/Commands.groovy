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
package com.mgmtp.jfunk.core.scripting.groovy

import com.mgmtp.jfunk.common.JFunkConstants
import com.mgmtp.jfunk.core.reporting.Reporter
import com.mgmtp.jfunk.core.scripting.ScriptContext

/**
 * Defines closures that can be used in Groovy scripts.
 *
 * @version $Id$
 */
public class Commands {
	ScriptContext scriptContext

	Commands(ScriptContext scriptContext) {
		this.scriptContext = scriptContext
	}

	Closure<File> chooseFile = { scriptContext.chooseFile(it) }

	Closure<String> chooseRandom = {
		scriptContext.chooseRandom('key', it)
	}

	Closure<Void> copyFormEntry = { sourceDataSet, sourceEntry, destinationDS, destinationEntry ->
		scriptContext.copyFormEntry(sourceDataSet, sourceEntry, destinationDS, destinationEntry)
	}

	Closure<Reporter> registerReporter = { reporter ->
		scriptContext.registerReporter(reporter)
	}

	Closure<Void> exceptional = { exceptions = [], closure ->
		scriptContext.exceptional(exceptions, closure)
	}

	@Deprecated
	Closure<Void> generate = { scriptContext.generate(it) }

	Closure<Void> prepareNextDataSet = { scriptContext.prepareNextDataSet(it) }

	Closure<String> get = {	scriptContext.get(it) }

	Closure<String> getScriptDir = { scriptContext.getScriptDir() }

	Closure<Void> load = { fileName, preserveExistingProps = false ->
		scriptContext.load(fileName, preserveExistingProps)
	}

	Closure<Void> log = { message ->
		scriptContext.log(message)
	}

	Closure<Void> module = { name, attributes = [:], closure ->
		scriptContext.module(name, attributes, closure)
	}

	Closure<Void> onError = { scriptContext.onError(it) }

	Closure<Void> optional = { closure ->
		scriptContext.optional(closure)
	}

	Closure<Void> processCsvFile = { csvFile, delimiter = ';', quoteChar = 0, charset = null, closure ->
		scriptContext.processCsvFile(csvFile, delimiter, quoteChar as char, charset, closure)
	}

	Closure<String> prompt = { configKey, message ->
		scriptContext.prompt(configKey, message)
	}

	Closure<Void> resetDataSource = { scriptContext.resetDataSource() }

	Closure<Void> resetError = { scriptContext.resetError() }

	Closure<Void> resetFixedData = { dataSetKey = null, entryKey = null ->
		scriptContext.resetFixedData(dataSetKey, entryKey)
	}

	Closure<Void> run = { scriptContext.run(it) }

	Closure<Void> runModule = { scriptContext.run(get('${' + JFunkConstants.TESTMODULE_CLASS + '}')) }

	Closure<Void> set = { key ->
		new SetBuilder(key: key)
	}

	Closure<SetEntryBuilder> setEntry = { key ->
		new SetEntryBuilder(entryKey: key)
	}

	private class SetBuilder {
		String key

		String to(def value) {
			scriptContext.set(key, value)
		}

		String nowTo(def value) {
			scriptContext.setNow(key, value)
		}

		ToEntryBuilder toEntry(String entryKey) {
			new ToEntryBuilder(scriptProperty: key, entryKey: entryKey)
		}
	}

	private class ToEntryBuilder {
		String scriptProperty
		String entryKey

		String inDataSet(String dataSetKey) {
			scriptContext.setToFormEntry(scriptProperty, dataSetKey, entryKey)
		}
	}

	private class SetEntryBuilder {
		String entryKey

		InDataSetBuilder inDataSet(String dataSetKey) {
			new InDataSetBuilder(dataSetKey: dataSetKey, entryKey: entryKey)
		}
	}

	private class InDataSetBuilder {
		String dataSetKey
		String entryKey

		def to(def value) {
			scriptContext.setFormEntry(dataSetKey, entryKey, value)
		}
	}
}
