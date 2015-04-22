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
/*
 * Demo script for jFunk in Groovy
 */
import org.openqa.selenium.By
import org.openqa.selenium.Keys

import com.mgmtp.jfunk.core.reporting.CsvReporter
import com.mgmtp.jfunk.samples.google.GoogleModule
import com.mgmtp.jfunk.web.step.CheckHtml4Pattern
import com.mgmtp.jfunk.web.step.JFunkWebElement
import com.mgmtp.jfunk.web.step.LoadPage
import com.mgmtp.jfunk.web.step.SendKeysStep

String url = 'http://www.google.com'

set 'archiving.mode' to 'all'
set 'archive.dir' to 'testruns/google'

//registerReporter CsvReporter.forDataSet('google').create()

//generate 'google'

//run new GoogleModule()

generate 'google'

module ('GoogleScriptModule', [dataSetKey: 'google']) {

	step new LoadPage(url)

	// Enter form value (form name=f, form field=q)
	searchTerm = get('${google searchTerm}')

	step new JFunkWebElement(By.name('q'), searchTerm)
	step new SendKeysStep(By.name('q'), Keys.ENTER)

	// Search for text on page
	step new CheckHtml4Pattern("(?s).*$searchTerm.*")
}
