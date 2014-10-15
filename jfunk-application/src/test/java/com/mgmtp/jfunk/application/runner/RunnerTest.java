package com.mgmtp.jfunk.application.runner;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author rnaegele
 */
public class RunnerTest {
	@Test
	public void testRun() throws IOException, InterruptedException {
		String xml = Resources.toString(Resources.getResource(getClass(), "testng_single_class.xml"), StandardCharsets.UTF_8);
		xml = xml.replace("${class}", "com.mgmtp.jfunk.application.runner.TreeTest");
		File suiteFile = File.createTempFile("jfunk_testng", ".xml", new File("target"));
		Files.write(xml, suiteFile, StandardCharsets.UTF_8);

		Process proc = new ProcessBuilder("cmd", "/c", "java", "cp", xml, "org.testng.TestNG", suiteFile.getPath(), "-d", "target\\test-output").directory(
				new File(".")).start();
		System.out.println(proc.waitFor());
	}
}
