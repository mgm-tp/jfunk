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
import com.mgmtp.jfunk.core.reporting.CsvReporter
import com.mgmtp.jfunk.core.scripting.TestModuleImpl

set 'archive.dir' to '${user.dir}/testruns/${script.name}'

registerReporter CsvReporter.forDataSet('example1').create()

100.times {
	generate 'example1'
	run new TestModuleImpl('example1')
}

