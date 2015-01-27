package com.mgmtp.jfunk.application.runner;

import com.mgmtp.jfunk.application.runner.exec.OsDependentExecutor;
import com.mgmtp.jfunk.application.runner.exec.TabExecuteResultHandler;
import com.mgmtp.jfunk.application.runner.exec.TabLogOutputStream;
import com.mgmtp.jfunk.application.runner.util.ConsoleQueueProcessor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFX.Type;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

	private final TabPane logPane;
	private final List<TabHolder> tabHolders = new ArrayList<>();
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	public ProcessController(final TabPane logPane) {
		this.logPane = logPane;
	}

	public void runTest(Path path, String method, final TestParameters testParams, TestType testType) throws IOException {
		String test = removeExtension(path.toString()).replaceAll("[/\\\\]", ".");
		if (method != null) {
			test += '#' + method;
		}

		logger.info("Running test process: {}", test);

		final ExecuteWatchdog watchdog = new ExecuteWatchdog(INFINITE_TIMEOUT);
		Executor executor = new OsDependentExecutor();

		final Tab tab = new Tab(test);
		tab.setGraphic(createImageView(getClass(), "spinner.gif"));
		tab.setClosable(true);
		tab.setOnCloseRequest(event -> {
			if (watchdog.isWatching()) {
				MonologFX msgBox = new MonologFX(Type.QUESTION);
				msgBox.setMessage("Process still running. Kill it?");
				msgBox.setModal(true);
				if (msgBox.show() == MonologFXButton.Type.YES) {
					logger.info("Destroying process: {}", tab.getText());
					try {
						watchdog.destroyProcess();
						executor.getStreamHandler().stop();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
				event.consume();
			}
		});
		tab.setOnClosed(event -> {
			int index = getTabIndex(tab);
			tabHolders.remove(index);
		});
		final TextArea console = new TextArea();
		console.setEditable(false);
		console.setWrapText(false);
//		console.setStyle("-fx-font-family: Courier New;");
		tab.setContent(console);
		logPane.getTabs().add(tab);
		logPane.getSelectionModel().select(tab);

		final BlockingQueue<String> consoleQueue = new LinkedBlockingDeque<>();
		final ScheduledFuture<?> future = scheduleQueuePolling(new ConsoleQueueProcessor(consoleQueue, console, 0));

		final TabHolder holder = new TabHolder(tab, console, executor, watchdog, consoleQueue);
		tabHolders.add(holder);

		CommandLine cmdl = testType.createCommandLine(path, method, testParams);
		executor.setWorkingDirectory(new File(".").getAbsoluteFile());

		logger.info("CommandLine: {}", cmdl);
		console.appendText(cmdl.toString() + "\n");

		executor.setWatchdog(watchdog);
		executor.setStreamHandler(new PumpStreamHandler(new TabLogOutputStream(consoleQueue)));
		executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
		executor.execute(cmdl, new TabExecuteResultHandler(tab, future, new ConsoleQueueProcessor(consoleQueue, console, 0)));
	}

	private ScheduledFuture<?> scheduleQueuePolling(final ConsoleQueueProcessor queueProcessor) {
		return executorService.scheduleWithFixedDelay(() -> queueProcessor.processQueue(), 0L, 75L, TimeUnit.MILLISECONDS);
	}

	public void killProcessInSelectedTab() {
		int index = logPane.getSelectionModel().getSelectedIndex();
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

	public boolean hasRunningProcesses() {
		return tabHolders.stream().anyMatch(tabHolder -> tabHolder.getWatchdog().isWatching());
	}

	public void shutdown() {
		tabHolders.forEach(tabHolder -> {
			try {
				tabHolder.getWatchdog().destroyProcess();
				tabHolder.getExecutor().getStreamHandler().stop();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		});
		executorService.shutdownNow();
	}
}
