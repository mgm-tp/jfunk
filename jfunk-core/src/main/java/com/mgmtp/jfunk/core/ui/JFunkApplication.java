package com.mgmtp.jfunk.core.ui;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		String fxmlFile = "fxml/app.fxml";
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

		Scene scene = new Scene(rootNode, 1024, 768);
		//		scene.getStylesheets().add("/styles/styles.css");

		stage.setTitle("jFunk Runner");
		stage.setScene(scene);
		stage.show();
	}

	@FXML
	private void initialize() {
		jFunkProps.getItems().addAll(retrieveAvailableJFunkProps());
		treeView.setRoot(new PathTreeItem(new ItemInfo(Paths.get("scripts"))));

		//		Files.walkFileTree(Paths.get("scripts"), new SimpleFileVisitor<Path>() {
		//
		//			@Override
		//			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
		//				logger.info(file.toString());
		//				return FileVisitResult.CONTINUE;
		//			}
		//		});
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
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("config"), filter)) {
			for (Path path : directoryStream) {
				fileNames.add(path.getFileName().toString());
			}
		} catch (IOException ex) {
			throw new JFunkException(ex);
		}

		return fileNames;
	}
}
