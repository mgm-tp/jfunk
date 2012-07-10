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

registerReporter CsvReporter.forDataSet('google').create()

generate 'google'

run new GoogleModule()

generate 'google'

module ('GoogleScriptModule', [dataSetKey: 'google']) {

	// Load page
	step new LoadPage(url)

	// Enter form value (form name=f, form field=q)
	searchTerm = get('${google searchTerm}')

	step new JFunkWebElement(By.name('q'), searchTerm)

	// Click submit button
	step new SendKeysStep(By.name('q'), Keys.ENTER)

	// Search for text on page
	step new CheckHtml4Pattern("(?s).*$searchTerm.*")
}
