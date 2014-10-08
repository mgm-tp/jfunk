package com.mgmtp.jfunk.application.runner;

import com.mgmtp.jfunk.application.runner.exec.OsDependentExecutor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import static java.nio.file.Paths.get;
import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class ProcessController {

	@FXML
	private Tab tab;

	@FXML
	private TextArea taLog;

	public void runTestWithMaven(Path path, String method, final Map<String, String> testProps) throws IOException {
		tab.setGraphic(new ImageView(new Image(getClass().getResource("spinner.gif").toExternalForm())));

		String test = removeExtension(path.toString()).replaceAll("[/\\\\]", ".");
		if (method != null) {
			test += '#' + method;
		}

		CommandLine cmdl = new CommandLine("mvn");
		cmdl.addArgument("test");
		cmdl.addArgument("-pl");
		cmdl.addArgument("jfunk-application");
		cmdl.addArgument("-am");
		cmdl.addArgument("-Dtest=" + test);
		cmdl.addArgument("-DfailIfNoTests=false");
		cmdl.addArgument("-DargLine=webdriver.key=ie");

//		StringBuilder sb = new StringBuilder("-DargLine=");
//		for (Entry<String, String> entry : testProps.entrySet()) {
//			sb.append("-D").append(entry.getKey()).append('=').append(entry.getValue());
//			sb.append(' ');
//		}
//		cmdl.addArgument(sb.toString().trim());

		taLog.appendText(cmdl.toString() + "\n\n");
		
		Executor executor = new OsDependentExecutor();
		executor.setWorkingDirectory(get(".").toAbsolutePath().normalize().getParent().toFile());

		LogOutputStream os = new LogOutputStream() {
			@Override
			protected void processLine(final String line, final int level) {
				Platform.runLater(new Runnable() {
					public void run() {
						taLog.appendText(line + '\n');
						Thread.yield();
					}
				});
			}
		};
		executor.setStreamHandler(new PumpStreamHandler(os));
		executor.execute(cmdl, new ExecuteResultHandler() {
			@Override
			public void onProcessComplete(final int exitValue) {
				Platform.runLater(new Runnable() {
					public void run() {
						tab.setGraphic(new ImageView(new Image(getClass().getResource("/com/famfamfam/silk/accept.png").toExternalForm())));
					}
				});
			}

			@Override
			public void onProcessFailed(final ExecuteException e) {
				Platform.runLater(new Runnable() {
					public void run() {
						tab.setGraphic(new ImageView(new Image(getClass().getResource("/com/famfamfam/silk/exclamation.png").toExternalForm())));
					}
				});
			}
		});
	}
}
