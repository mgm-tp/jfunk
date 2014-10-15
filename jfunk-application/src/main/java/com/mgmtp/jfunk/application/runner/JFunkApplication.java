package com.mgmtp.jfunk.application.runner;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mgmtp.jfunk.common.exception.JFunkException;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.collect.Lists.newArrayList;
import static com.mgmtp.jfunk.application.runner.util.UiUtils.createImage;
import static com.mgmtp.jfunk.application.runner.util.UiUtils.createImageView;
import static com.mgmtp.jfunk.application.runner.util.UiUtils.findTreeItem;
import static com.mgmtp.jfunk.application.runner.util.UiUtils.setExpanded;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Paths.get;
import static java.util.Collections.emptySet;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

/**
 * @author rnaegele
 */
public class JFunkApplication extends Application {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@FXML
	private Slider threads;

	@FXML
	private CheckBox parallel;

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

	@FXML
	private GridPane testPropsPane;

	private Map<String, ComboBox<String>> testPropsBoxes = new HashMap<>();

	private final ProcessController procCtrl = new ProcessController();

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stage.setIconified(false);
				saveState(stage);
			}
		});
		for (Screen screen : Screen.getScreens()) {
			screen.getBounds();
		}
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		try (InputStream is = getClass().getResourceAsStream("fxml/app.fxml")) {
			Parent rootNode = (Parent) loader.load(is);
			Scene scene = new Scene(rootNode, 1024d, 768d);
			scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F9), new Runnable() {
						@Override
						public void run() {
							btnRun.arm();
							PauseTransition pt = new PauseTransition(Duration.millis(200));
							pt.setOnFinished(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									btnRun.fire();
									btnRun.disarm();
								}
							});
							pt.play();
						}
					}
			);

			stage.setTitle("jFunk Runner");
			stage.getIcons().add(createImage("jFunk.png"));
			stage.setScene(scene);
			loadState(stage);
			stage.show();

			// If the windows was iconified before being closed last, on Windows -32000 is stored for x and y values
			// which are way outside any screen bounds. The code below restores the window to its default bounds in this case.
			ObservableList<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
			if (screens.isEmpty()) {
				stage.setWidth(1024d);
				stage.setHeight(768d);
				stage.centerOnScreen();
			}
		}
	}

	@FXML
	private void initialize() throws IOException {
		treeView.setRoot(new TreeItem<ItemInfo>(new ItemInfo("jFunk", ItemInfoType.LABEL)));

		testPropsPane.setHgap(10d);
		testPropsPane.setVgap(10d);
		try (Reader reader = newBufferedReader(get("config", "runner.json"), StandardCharsets.UTF_8)) {
			Gson gson = new GsonBuilder().create();
			RunnerConfig cfg = gson.fromJson(reader, RunnerConfig.class);
			int row = 1;
			for (Entry<String, List<String>> entry : cfg.getTestProperties().entrySet()) {
				String key = entry.getKey();
				testPropsPane.add(new Label(key), 1, row);
				ComboBox<String> values = new ComboBox<>();
				testPropsBoxes.put(key, values);
				values.setId(key);
				testPropsPane.add(values, 2, row++);
				for (String value : entry.getValue()) {
					values.getItems().add(value);
				}
			}

			retrieveAvailableJFunkProps();
			btnRun.setGraphic(createImageView("com/famfamfam/silk/control_play.png"));
			btnExpandAll.setGraphic(createImageView("com/famfamfam/silk/add.png"));
			btnCollapseAll.setGraphic(createImageView("com/famfamfam/silk/delete.png"));
			retrieveTestClassesAndMethods();
			retrieveGroovyScripts(cfg.getGroovyScriptDirs());

			treeView.setCellFactory(new Callback<TreeView<ItemInfo>, TreeCell<ItemInfo>>() {

				private EventHandler<MouseEvent> runHandler = new EventHandler<MouseEvent>() {
					@Override
					public void handle(final MouseEvent mouseEvent) {
						if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
							if (mouseEvent.getClickCount() == 2) {
								btnRun.fire();
							}
						}
					}
				};

				@Override
				public TreeCell<ItemInfo> call(final TreeView<ItemInfo> objectTreeView) {
					return new TreeCell<ItemInfo>() {
						@Override
						protected void updateItem(final ItemInfo itemInfo, final boolean empty) {
							super.updateItem(itemInfo, empty);
							if (!empty) {
								String res;
								switch (itemInfo.getType()) {
									case LABEL:
										setText(itemInfo.getValue());
										res = "com/famfamfam/silk/folder.png";
										break;
									case TEST_CLASS:
										setText(removeExtension(itemInfo.getValue()));
										res = "com/famfamfam/silk/page.png";
										break;
									case TEST_METHOD:
										setText(itemInfo.getValue());
										setOnMouseClicked(runHandler);
										res = "com/famfamfam/silk/page_white_code_red.png";
										break;
									case TEST_SCRIPT:
										setText(itemInfo.getValue());
										setOnMouseClicked(runHandler);
										res = "com/famfamfam/silk/page_white_code_red.png";
										break;
									default:
										throw new IllegalStateException("Default case not handled for enum: " + itemInfo.getType());
								}
								setGraphic(createImageView(res));
							}
						}
					};
				}
			});

			setExpanded(treeView.getRoot(), true);
		}
	}

	private void loadState(Stage stage) {
		try (Reader reader = newBufferedReader(get("config", "uistate.json"), StandardCharsets.UTF_8)) {
			UiState state = new Gson().fromJson(reader, UiState.class);
			stage.setX(state.getWindowX());
			stage.setY(state.getWindowY());
			stage.setWidth(state.getWindowWidth());
			stage.setHeight(state.getWindowHeight());
			threads.setValue(state.getThreads());
			parallel.setSelected(state.isParallel());
			String props = state.getjFunkProps();
			if (jFunkProps.getItems().contains(props)) {
				jFunkProps.setValue(props);
			}
			for (Entry<String, ComboBox<String>> entry : testPropsBoxes.entrySet()) {
				String key = entry.getKey();
				String value = state.getTestProps().get(key);
				ComboBox<String> comboBox = testPropsBoxes.get(key);
				if (comboBox.getItems().contains(value)) {
					comboBox.setValue(value);
				}
			}
		} catch (Exception ex) {
			logger.error("Could not load UI state: {}", ex.toString());
		}
	}

	private void saveState(Stage stage) {
		try (Writer writer = newBufferedWriter(get("config", "uistate.json"), StandardCharsets.UTF_8)) {
			UiState state = new UiState();
			state.setWindowX(stage.getX());
			state.setWindowY(stage.getY());
			state.setWindowWidth(stage.getWidth());
			state.setWindowHeight(stage.getHeight());
			state.setThreads(threads.getValue());
			state.setParallel(parallel.isSelected());
			state.setjFunkProps(jFunkProps.getValue());
			for (Entry<String, ComboBox<String>> entry : testPropsBoxes.entrySet()) {
				state.getTestProps().put(entry.getKey(), entry.getValue().getValue());
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(state, writer);
		} catch (IOException ex) {
			logger.error("Could not write UI state", ex);
		}
	}

	private void retrieveAvailableJFunkProps() {
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

		jFunkProps.getItems().addAll(fileNames);
		jFunkProps.getSelectionModel().select(0);
	}

	private Set<Path> findPaths(final Path startDir, final String syntaxAndPattern) throws IOException {
		if (!Files.exists(startDir)) {
			logger.warn("Path '{}' does not exist. Cannot retrieve files for this path with 'syntaxAndPattern': {}", startDir, syntaxAndPattern);
			return emptySet();
		}
		final Set<Path> paths = new TreeSet<>();
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(syntaxAndPattern);
		walkFileTree(startDir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				if (pathMatcher.matches(file)) {
					paths.add(startDir.relativize(file));
				}
				return FileVisitResult.CONTINUE;
			}
		});
		return paths;
	}

	private void retrieveTestClassesAndMethods() throws IOException {
		Path start = get("target/test-classes");
		final Set<Path> testClasses = findPaths(start, "glob:**/*Test.class");
		TreeItem<ItemInfo> root = new TreeItem<>(new ItemInfo("Test Classes", ItemInfoType.LABEL));
		treeView.getRoot().getChildren().add(root);

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
			Set<Method> testMethods = new TreeSet<>(new Comparator<Method>() {
				@Override
				public int compare(final Method m1, final Method m2) {
					return m1.getName().compareTo(m2.getName());
				}
			});
			String fqcn = removeExtension(pathString.replaceAll("[/\\\\]", "."));

			try {
				logger.info("Searching TestNG tests...");
				Class<? extends Annotation> testNgAnnotation = Class.forName("org.testng.annotations.Test").asSubclass(Annotation.class);
				testMethods.addAll(getMethods(Class.forName(fqcn, false, testClassLoader), withAnnotation(testNgAnnotation)));
			} catch (ClassNotFoundException ex) {
				logger.info("TestNG not on class path: {}", ex.getMessage());
			}

			try {
				logger.info("Searching JUnit tests...");
				Class<? extends Annotation> jUnitAnnotation = Class.forName("org.junit.Test").asSubclass(Annotation.class);
				testMethods.addAll(getMethods(Class.forName(fqcn, false, testClassLoader), withAnnotation(jUnitAnnotation)));
			} catch (ClassNotFoundException ex) {
				logger.info("JUnit not on class path: {}", ex.getMessage());
			}

			for (Method testMethod : testMethods) {
				current.getChildren().add(new TreeItem<ItemInfo>(new ItemInfo(testMethod.getName(), ItemInfoType.TEST_METHOD)));
			}
		}
	}

	private void retrieveGroovyScripts(final List<String> groovyScriptDirs) throws IOException {
		Set<Path> groovyScripts = new TreeSet<>();
		for (String dir : groovyScriptDirs) {
			Path startDir = get(dir);
			Set<Path> paths = findPaths(startDir, "glob:**/*.groovy");
			for (Path path : paths) {
				groovyScripts.add(startDir.resolve(path));
			}
		}

		TreeItem<ItemInfo> root = new TreeItem<>(new ItemInfo("Groovy Scripts", ItemInfoType.LABEL));
		treeView.getRoot().getChildren().add(root);

		for (Path groovyScript : groovyScripts) {
			TreeItem<ItemInfo> current = root;
			String pathString = groovyScript.toString();
			List<String> split = newArrayList(pathString.split("[/\\\\]"));
			System.out.println(Arrays.asList(split));
			String previousPathElement = null;
			for (Iterator<String> it = split.iterator(); it.hasNext(); ) {
				String s = it.next();
				Path path = previousPathElement != null ? get(previousPathElement, s) : get(s);
				previousPathElement = path.toString();

				final TreeItem<ItemInfo> item = new TreeItem<>(new ItemInfo(path, it.hasNext() ? ItemInfoType.LABEL : ItemInfoType.TEST_SCRIPT));
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
		}
	}

	public void runTest(final ActionEvent e) throws Exception {
		TreeItem<ItemInfo> item = treeView.getSelectionModel().getSelectedItem();
		if (item != null) {
			ItemInfoType type = item.getValue().getType();
			switch (type) {
				case TEST_CLASS:
					runTest(item.getValue().getPath(), null, TestType.JUNIT);
					break;
				case TEST_METHOD:
					runTest(item.getParent().getValue().getPath(), item.getValue().getValue(), TestType.JUNIT);
					break;
				case TEST_SCRIPT:
					runTest(item.getParent().getValue().getPath(), item.getValue().getValue(), TestType.GROOVY);
					break;
				default:
					// nothing
			}
		}
	}

	private void runTest(final Path path, final String method, final TestType testType) throws Exception {
		procCtrl.runTest(path, method, Maps.transformValues(testPropsBoxes, new Function<ComboBox<String>, String>() {
			@Override
			public String apply(final ComboBox<String> input) {
				return input.getValue();
			}
		}), testType);
	}

	public void expandAll(final ActionEvent e) {
		setExpanded(treeView.getRoot(), true);
	}

	public void collapseAll(final ActionEvent e) {
		setExpanded(treeView.getRoot(), false);
	}

	public void killTestProcess(final ActionEvent e) {
		procCtrl.killProcessInSelectedTab();
	}
}
