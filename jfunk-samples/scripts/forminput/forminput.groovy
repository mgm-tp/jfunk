/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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
