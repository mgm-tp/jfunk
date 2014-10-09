package com.mgmtp.jfunk.application.runner;

import com.mgmtp.jfunk.application.runner.exec.OsDependentExecutor;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFX.Type;
import jfxtras.labs.dialogs.MonologFXBuilder;
import jfxtras.labs.dialogs.MonologFXButton;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.mgmtp.jfunk.application.runner.util.UiUtils.createImage;
import static com.mgmtp.jfunk.application.runner.util.UiUtils.createImageView;
import static java.nio.file.Paths.get;
import static org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT;
import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class ProcessController {

	private TabPane tabPane = new TabPane();

	private Stage consoleWindow;

	private List<TabHolder> tabHolders = new ArrayList<>();

	public ProcessController() {
		tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
	}

	public void showConsoleWindow() {
		if (consoleWindow == null) {
			Stage stage = new Stage();
			stage.setTitle("jFunk Log Viewer");
			stage.getIcons().add(createImage("jFunk.png"));

			Scene scene = new Scene(tabPane, 1024, 768);
			stage.setScene(scene);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(final WindowEvent windowEvent) {
					MonologFX msgBox = MonologFXBuilder.create()
													   .titleText("Confirmation")
													   .message("Close and kill running processes?")
													   .type(Type.QUESTION)
													   .build();
					if (msgBox.showDialog() == MonologFXButton.Type.YES) {
						for (TabHolder tabHolder : tabHolders) {
							tabHolder.getWatchdog().destroyProcess();
						}
						tabHolders.clear();
						tabPane.getTabs().clear();
					} else {
						windowEvent.consume();
					}
				}
			});
			consoleWindow = stage;
		}
		consoleWindow.show();
		consoleWindow.toFront();
	}

	public void runTestWithMaven(Path path, String method, final Map<String, String> testProps) throws IOException {
		String test = removeExtension(path.toString()).replaceAll("[/\\\\]", ".");
		if (method != null) {
			test += '#' + method;
		}

		final ExecuteWatchdog watchdog = new ExecuteWatchdog(INFINITE_TIMEOUT);

		final Tab tab = new Tab(test);
		tab.setGraphic(createImageView(getClass(), "spinner.gif"));
		tab.setClosable(false);
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(final Event event) {
				int index = getTabIndex(tab);
				tabHolders.remove(index);
			}
		});
		final TextArea console = new TextArea();
		console.setEditable(false);
		tab.setContent(console);
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);

		TabHolder holder = new TabHolder(tab, console, watchdog);
		tabHolders.add(holder);

		CommandLine cmdl = new CommandLine("mvn");
		cmdl.addArgument("test");
		cmdl.addArgument("-pl");
		cmdl.addArgument("jfunk-application");
//		cmdl.addArgument("-X");
		cmdl.addArgument("-Dtest=" + test);
		cmdl.addArgument("-DfailIfNoTests=false");

		Executor executor = new OsDependentExecutor();
		executor.setWorkingDirectory(get(".").toAbsolutePath().normalize().getParent().toFile());

//		String mavenOpts = nullToEmpty(System.getenv("MAVEN_OPTS"));
//		StrBuilder sbMavenOpts = new StrBuilder(200);
//		sbMavenOpts.append(mavenOpts);
		for (Entry<String, String> entry : testProps.entrySet()) {
			cmdl.addArgument("-D" + entry.getKey() + '=' + entry.getValue());
		}
//
//		Map<String, String> env = new HashMap<>(System.getenv());
//		env.put("MAVEN_OPTS", sbMavenOpts.toString());

		console.appendText(cmdl.toString() + "\n");
//		taLog.appendText("MAVEN_OPTS: " + sbMavenOpts + "\n\n");

		LogOutputStream os = new LogOutputStream() {
			private int lineCounter = 0;

			@Override
			protected void processLine(final String line, final int level) {

				Platform.runLater(new Runnable() {
					public void run() {
//						lineCounter++;
//						if (lineCounter > 1000) {
//							taLog.deleteText(0, taLog.getText().indexOf('\n'));
//						}
						console.appendText(line + '\n');
					}
				});
			}
		};
		executor.setWatchdog(watchdog);
		executor.setStreamHandler(new PumpStreamHandler(os));
		executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
		executor.execute(cmdl, new ExecuteResultHandler() {
			@Override
			public void onProcessComplete(final int exitValue) {
				Platform.runLater(new Runnable() {
					public void run() {
						tab.setGraphic(createImageView("com/famfamfam/silk/accept.png"));
						tab.setClosable(true);
					}
				});
			}

			@Override
			public void onProcessFailed(final ExecuteException e) {
				Platform.runLater(new Runnable() {
					public void run() {
						tab.setGraphic(createImageView("com/famfamfam/silk/exclamation.png"));
						tab.setClosable(true);
						// TODO logger
						e.printStackTrace();
					}
				});
			}
		});

		showConsoleWindow();
	}

	public void killProcessInSelectedTab() {
		int index = tabPane.getSelectionModel().getSelectedIndex();
		tabHolders.get(index).getWatchdog().destroyProcess();
	}

	private int getTabIndex(Tab tab) {
		for (int i = 0; i < tabHolders.size(); i++) {
			if (tabHolders.get(i).getTab() == tab) {
				return i;
			}
		}
		return -1;
	}
}
