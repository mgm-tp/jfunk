package com.mgmtp.jfunk.application.runner.exec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.launcher.CommandLauncher;
import org.apache.commons.exec.launcher.WinNTCommandLauncher;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author rnaegele
 */
public class OsDependentExecutor extends DefaultExecutor {

	/**
	 * Creates a process that runs a command.
	 *
	 * @param command the command to run
	 * @param env     the environment for the command
	 * @param dir     the working directory for the command
	 * @return the process started
	 * @throws java.io.IOException forwarded from the particular launcher used
	 */
	protected Process launch(final CommandLine command, final Map env, final File dir) throws IOException {
		if (dir != null && !dir.exists()) {
			throw new IOException(dir + " doesn't exist.");
		}

		CommandLine cmdToExecute;
		if (OS.isFamilyWindows()) {
			cmdToExecute = new CommandLine("cmd");
			cmdToExecute.addArgument("/c");
			cmdToExecute.addArguments(command.toStrings());
		} else {
			cmdToExecute = command;
		}
		return new ProcessBuilderLauncher().exec(cmdToExecute, env, dir);
	}

	static class ProcessBuilderLauncher implements CommandLauncher {

		@Override
		public Process exec(final CommandLine command, final Map env) throws IOException {
			return exec(command, env, new File("."));
		}

		@Override
		public Process exec(final CommandLine command, final Map env, final File workingDir) throws IOException {
			return new ProcessBuilder().directory(workingDir).command(command.toStrings()).start();
		}

		@Override
		public boolean isFailure(final int exitValue) {
			return exitValue != 0;
		}
	}
}
