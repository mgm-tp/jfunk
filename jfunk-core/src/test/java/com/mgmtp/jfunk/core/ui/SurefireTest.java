package com.mgmtp.jfunk.core.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.surefire.util.DirectoryScanner;
import org.apache.maven.surefire.util.ScanResult;
import org.testng.annotations.Test;

/**
 * @author rnaegele
 */
public class SurefireTest {

	@Test
	public void testScan() throws IOException {
		// use target as people can configure ide to compile in an other place than maven
		File baseDir = new File("target/test-classes").getCanonicalFile();
		//		List<String> include = new ArrayList<>();
		//		include.add("**/*ZT*A.java");
		//		List<String> exclude = new ArrayList<>();

		DirectoryScanner surefireDirectoryScanner = new DirectoryScanner(baseDir, null, null, null);

		ScanResult classNames = surefireDirectoryScanner.scan();
		int size = classNames.size();
		List<String> testClasses = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			testClasses.add(classNames.getClassName(i));
		}

		//		assertNotNull(classNames);
		System.out.println(testClasses);
		//		assertEquals(3, classNames.size());
		//
		//		Properties props = new Properties();
		//		classNames.writeTo(props);
		//		assertEquals(3, props.size());
	}
}
