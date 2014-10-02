package com.mgmtp.jfunk.application.runner;

import com.google.common.base.Predicate;
import com.mgmtp.jfunk.common.cli.CliUtils;
import com.mgmtp.jfunk.common.exception.JFunkException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Lists.newArrayList;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Paths.get;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

/**
 * @author rnaegele
 */
public class JFunkApplication extends Application {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@FXML
	private ComboBox<String> jFunkProps;

	@FXML
	private TreeView<ItemInfo> treeView;

	@FXML
	private Button btnRun;

	@FXML
	private Button btnExpandAll;

	@FXML
	private Button btnCollapseAll;

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		try (InputStream is = getClass().getResourceAsStream("fxml/app.fxml")) {
			Parent rootNode = (Parent) loader.load(is);
			Scene scene = new Scene(rootNode, 1024, 768);
			stage.setTitle("jFunk Runner");
			stage.getIcons().add(new Image(getClass().getResource("/jFunk.png").toExternalForm()));
			stage.setScene(scene);
			stage.show();
		}
	}

	@FXML
	private void initialize() throws IOException {
		jFunkProps.getItems().addAll(retrieveAvailableJFunkProps());
		btnRun.setGraphic(new ImageView(new Image(getClass().getResource("/com/famfamfam/silk/control_play.png").toExternalForm())));
		btnExpandAll.setGraphic(new ImageView(new Image(getClass().getResource("/com/famfamfam/silk/arrow_out.png").toExternalForm())));
		btnCollapseAll.setGraphic(new ImageView(new Image(getClass().getResource("/com/famfamfam/silk/arrow_in.png").toExternalForm())));
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
		final Set<Path> testClasses = new TreeSet<>();
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*Test.class");
		final Path start = get("target/test-classes");
		walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				if (pathMatcher.matches(file)) {
					testClasses.add(start.relativize(file));
				}
				return FileVisitResult.CONTINUE;
			}
		});

		TreeItem<ItemInfo> root = new TreeItem<>(new ItemInfo("Test Classes", ItemInfoType.LABEL));
		treeView.setRoot(root);

		ClassLoader testClassLoader = new URLClassLoader(new URL[]{start.toUri().toURL()});

		for (Path testClass : testClasses) {
			TreeItem<ItemInfo> current = root;
			String pathString = testClass.toString();
			List<String> split = newArrayList(pathString.split("[/\\\\]"));
			System.out.println(Arrays.asList(split));
			String previousPathElement = null;
			for (Iterator<String> it = split.iterator(); it.hasNext(); ) {
				String s = it.next();
				Path path = previousPathElement != null ? get(previousPathElement, s) : get(s);
				previousPathElement = path.toString();

				final TreeItem<ItemInfo> item = new TreeItem<>(new ItemInfo(path, it.hasNext() ? ItemInfoType.LABEL : ItemInfoType.TEST_CLASS));
				TreeItem<ItemInfo> existing = findTreeItem(root, new Predicate<TreeItem<ItemInfo>>() {
					@Override
					public boolean apply(final TreeItem<ItemInfo> input) {
						return input.getValue().getValue().equals(item.getValue().getValue());
					}
				});
				if (existing == null) {
					current.getChildren().add(item);
					current = item;
				} else {
					System.out.println("found: " + existing);
					current = existing;
				}
			}
			Set<Method> testMethods = null;
			try {
				String fqcn = removeExtension(pathString.replaceAll("[/\\\\]", "."));
				testMethods = getMethods(Class.forName(fqcn, false, testClassLoader), or(withAnnotation(Test.class),
						withAnnotation(org.testng.annotations.Test.class)));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			for (Method testMethod : testMethods) {
				current.getChildren().add(new TreeItem<ItemInfo>(new ItemInfo(testMethod.getName(), ItemInfoType.TEST_METHOD)));
			}
		}

		treeView.setCellFactory(new Callback<TreeView<ItemInfo>, TreeCell<ItemInfo>>() {
			@Override
			public TreeCell<ItemInfo> call(final TreeView<ItemInfo> objectTreeView) {
				return new TreeCell<ItemInfo>() {
					@Override
					protected void updateItem(final ItemInfo itemInfo, final boolean empty) {
						super.updateItem(itemInfo, empty);
						if (!empty) {
							setText(itemInfo.getValue());
							String res;
							switch (itemInfo.getType()) {
								case LABEL:
									res = "/com/famfamfam/silk/folder.png";
									break;
								case TEST_CLASS:
									res = "/com/famfamfam/silk/page.png";
									break;
								case TEST_METHOD:
									res = "/com/famfamfam/silk/page_white_code_red.png";
									break;
								default:
									throw new IllegalStateException("Default case not handled for enum: " + itemInfo.getType());
							}
							setGraphic(new ImageView(new Image(getClass().getResource(res).toExternalForm())));
						}
					}
				};
			}
		});
	}

	private void traverseTreeItem(TreeItem<ItemInfo> startNode, TreeItemVisitor<ItemInfo> visitor) {
		if (visitor.visit(startNode)) {
			if (!startNode.isLeaf()) {
				for (TreeItem<ItemInfo> treeItem : startNode.getChildren()) {
					traverseTreeItem(treeItem, visitor);
				}
			}
		}
	}

	private TreeItem<ItemInfo> findTreeItem(TreeItem<ItemInfo> startNode, Predicate<TreeItem<ItemInfo>> predicate) {
		for (TreeItem<ItemInfo> node : startNode.getChildren()) {
			if (predicate.apply(node)) {
				return node;
			}
			TreeItem<ItemInfo> result = findTreeItem(node, predicate);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private void runTestWithSurefire() {

	}

	private void setExpanded(final TreeItem<ItemInfo> startNode, final boolean expanded) {
		traverseTreeItem(startNode, new TreeItemVisitor<ItemInfo>() {
			@Override
			public boolean visit(final TreeItem<ItemInfo> treeItem) {
				if (!treeItem.isLeaf()) {
					treeItem.setExpanded(expanded);
				}
				return true;
			}
		});
	}

	public void runTest(ActionEvent e) throws Exception {
//		TreeItem<ItemInfo> item = (TreeItem<ItemInfo>) e.getSource();
//		item.getValue().getType();
		Commandline cli = new Commandline();
		cli.setExecutable("mvn");
		cli.setWorkingDirectory(get(".").toAbsolutePath().normalize().getParent().toFile());
		cli.addSystemEnvironment();
//		cli.createArg().setValue("test");
//		cli.createArg().setValue("-pl");
//		cli.createArg().setValue("jfunk-application");
//		cli.createArg().setValue("-am");
//		cli.createArg().setValue("-Dtest=com.mgmtp.jfunk.application.runner.TreeTest#testFind");
//		cli.createArg().setValue("-DfailIfNoTests=false");
		CliUtils.executeCommandLine(cli);

		new ProcessBuilder().command("cmd.exe", "/c", "dir").directory(get(".").toAbsolutePath().normalize().getParent().toFile()).start();
	}

	public void expandAll(ActionEvent e) {
		setExpanded(treeView.getRoot(), true);
	}

	public void collapseAll(ActionEvent e) {
		setExpanded(treeView.getRoot(), false);
	}
}
