package com.mgmtp.jfunk.application.runner;

import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.TreeMultiset;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.mgmtp.jfunk.common.exception.JFunkException;

/**
 * @author rnaegele
 */
public class JFunkApplication extends Application {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@FXML
	private ComboBox<String> jFunkProps;

	@FXML
	private TreeView<ItemInfo> treeView;

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream("fxml/app.fxml"));

		Scene scene = new Scene(rootNode, 1024, 768);
		stage.setTitle("jFunk Runner");
		stage.getIcons().add(new Image(getClass().getResource("/jFunk.png").toExternalForm()));
		stage.setScene(scene);
		stage.show();
	}

	@FXML
	private void initialize() throws IOException {
		jFunkProps.getItems().addAll(retrieveAvailableJFunkProps());
		TreeItem<ItemInfo> root = new PathTreeItem(new ItemInfo("jFunk"), null);
		root.setGraphic(new ImageView(new Image(getClass().getResource("/com/famfamfam/silk/computer.png").toExternalForm())));
		FileSystem fs = FileSystems.getDefault();
		ObservableList<TreeItem<ItemInfo>> children = root.getChildren();
		children.add(new PathTreeItem(new ItemInfo("Groovy Scripts", get("scripts")), fs.getPathMatcher("glob:scripts/**/*.groovy")));
		children.add(new PathTreeItem(new ItemInfo("Unit Tests", get("target/test-classes")), fs.getPathMatcher("glob:**/*Test.class")));
		treeView.setRoot(root);

		findTestClassesAndMethods();
	}

	private List<String> retrieveAvailableJFunkProps() {
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(final Path file) throws IOException {
				String name = file.getFileName().toString();
				return name.startsWith("jfunk.") && name.endsWith(".properties");
			}
		};

		List<String> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = newDirectoryStream(get("config"), filter)) {
			for (Path path : directoryStream) {
				fileNames.add(path.getFileName().toString());
			}
		} catch (IOException ex) {
			throw new JFunkException(ex);
		}

		return fileNames;
	}

	private void findTestClassesAndMethods() throws IOException {
//		walkFileTree(get("target/test-classes"), new SimpleFileVisitor<Path>() {
//			@Override
//			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
//				return super.preVisitDirectory(dir, attrs);
//			}
//
//			@Override
//			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
//				return super.visitFile(file, attrs);
//			}
//		});

		URL url = get("target/test-classes").toUri().toURL();
		ClassLoader loader = new URLClassLoader(new URL[] { url });
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().addClassLoader(loader).addScanners(new MethodAnnotationsScanner()).addUrls(url));
		Set<Method> set = reflections.getMethodsAnnotatedWith(Test.class);
		System.out.println(set);



	}

	private void runTestWithSurefire() {

	}
}
