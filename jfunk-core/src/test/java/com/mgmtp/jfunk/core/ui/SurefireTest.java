package com.mgmtp.jfunk.core.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

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

		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*Test.class");
		Files.walkFileTree(Paths.get("target/test-classes"), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				if (matcher.matches(file)) {
					System.out.println(file);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});

		//		DirectoryScanner surefireDirectoryScanner = new DirectoryScanner(baseDir, null, null, null);
		//
		//		ScanResult classNames = surefireDirectoryScanner.scan();
		//		int size = classNames.size();
		//		List<String> testClasses = new ArrayList<>(size);
		//		for (int i = 0; i < size; ++i) {
		//			testClasses.add(classNames.getClassName(i));
		//		}

		//		assertNotNull(classNames);
		//		System.out.println(testClasses);
		//		assertEquals(3, classNames.size());
		//
		//		Properties props = new Properties();
		//		classNames.writeTo(props);
		//		assertEquals(3, props.size());
	}
}
