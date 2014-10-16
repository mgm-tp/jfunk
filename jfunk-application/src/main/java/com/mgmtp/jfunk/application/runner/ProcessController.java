package com.mgmtp.jfunk.application.runner;

import com.mgmtp.jfunk.application.runner.exec.OsDependentExecutor;
import com.mgmtp.jfunk.application.runner.exec.TabExecuteResultHandler;
import com.mgmtp.jfunk.application.runner.exec.TabLogOutputStream;
import com.mgmtp.jfunk.application.runner.util.ConsoleQueueProcessor;
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
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.mgmtp.jfunk.application.runner.util.UiUtils.createImage;
import static com.mgmtp.jfunk.application.runner.util.UiUtils.createImageView;
import static org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT;
import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * Controls test processes.
 *
 * @author rnaegele
 * @since 3.1.0
 */
public class ProcessController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final TabPane tabPane = new TabPane();
	private final List<TabHolder> tabHolders = new ArrayList<>();
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	private final Stage primaryStage;

	private Stage consoleWindow;

	public ProcessController(final Stage primaryStage) {
		this.primaryStage = primaryStage;
		tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
	}

	public void showConsoleWindow() {
		if (consoleWindow == null) {
			logger.info("Creating console window...");
			Stage stage = new Stage();
			stage.initOwner(primaryStage);
			stage.setTitle("jFunk Log Viewer");
			stage.getIcons().add(createImage("jFunk.png"));

			Scene scene = new Scene(tabPane, 1024, 768);
			stage.setScene(scene);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(final WindowEvent windowEvent) {
					boolean procsRunning = false;
					for (TabHolder tabHolder : tabHolders) {
						if (tabHolder.getWatchdog().isWatching()) {
							procsRunning = true;
							break;
						}
					}
					if (procsRunning) {
						MonologFX msgBox = MonologFXBuilder.create()
														   .titleText("Confirmation")
														   .message("Close and kill running processes?")
														   .type(Type.QUESTION)
														   .build();
						if (msgBox.showDialog() == MonologFXButton.Type.YES) {
							for (TabHolder tabHolder : tabHolders) {
								logger.info("Destroying process: {}", tabHolder.getTab().getText());
								tabHolder.getWatchdog().destroyProcess();
							}
							logger.info("Closing all tabs...");
							tabHolders.clear();
							tabPane.getTabs().clear();
						} else {
							windowEvent.consume();
						}
					}
				}
			});
			consoleWindow = stage;
		}
		logger.info("Showing console window...");
		consoleWindow.show();
		consoleWindow.toFront();
	}

	public void runTest(Path path, String method, final TestParameters testParams, TestType testType) throws IOException {
		String test = removeExtension(path.toString()).replaceAll("[/\\\\]", ".");
		if (method != null) {
			test += '#' + method;
		}

		logger.info("Running test process: {}", test);

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
		console.setWrapText(false);
		console.setStyle("-fx-font-family: Courier New;");
		tab.setContent(console);
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);

		final BlockingQueue<String> consoleQueue = new LinkedBlockingDeque<>();
		final ScheduledFuture<?> future = scheduleQueuePolling(new ConsoleQueueProcessor(consoleQueue, console, 10));

		final TabHolder holder = new TabHolder(tab, console, watchdog, consoleQueue);
		tabHolders.add(holder);

		CommandLine cmdl = testType.createCommandLine(path, method, testParams);

		Executor executor = new OsDependentExecutor();
		executor.setWorkingDirectory(new File(".").getAbsoluteFile());

		logger.info("CommandLine: {}", cmdl);
		console.appendText(cmdl.toString() + "\n");

		executor.setWatchdog(watchdog);
		executor.setStreamHandler(new PumpStreamHandler(new TabLogOutputStream(consoleQueue)));
		executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
		executor.execute(cmdl, new TabExecuteResultHandler(tab, future, new ConsoleQueueProcessor(consoleQueue, console, 0)));

		showConsoleWindow();
	}

	private ScheduledFuture<?> scheduleQueuePolling(final ConsoleQueueProcessor queueProcessor) {
		return executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				queueProcessor.processQueue();
			}
		}, 100L, 100L, TimeUnit.MILLISECONDS);
	}

	public void killProcessInSelectedTab() {
		int index = tabPane.getSelectionModel().getSelectedIndex();
		TabHolder tabHolder = tabHolders.get(index);
		logger.info("Killing process: {}", tabHolder.getTab().getText());
		tabHolder.getWatchdog().destroyProcess();
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
