import com.mgmtp.jfunk.core.reporting.CsvReporter
import com.mgmtp.jfunk.core.scripting.TestModuleImpl

set 'archive.dir' to '${user.dir}/testruns/${script.name}'

registerReporter CsvReporter.forDataSet('example1').create()

100.times {
	generate 'example1'
	run new TestModuleImpl('example1')
}

