/**
 * Copyright (c) 2015 mgm technology partners GmbH
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
import com.mgmtp.jfunk.samples.forminput.ForminputModule
import com.mgmtp.jfunk.web.WebConstants

set WebConstants.WEBDRIVER_KEY to WebConstants.WEBDRIVER_HTMLUNIT

registerReporter CsvReporter.forDataSet('forminput').delimitedBy(';').quotedWith(0 as char).writtenTo('forminput.csv').create()
registerReporter CsvReporter.withHeaders(['forminput stringProperty', 'forminput booleanProperty', 'webdriver.key']).create()

generate 'forminput'

set 'archiving.mode' to 'all'
set 'archive.dir' to 'testruns/forminput'

3.times {
	optional {
		generate 'forminput'
		run new ForminputModule()
	}
}
