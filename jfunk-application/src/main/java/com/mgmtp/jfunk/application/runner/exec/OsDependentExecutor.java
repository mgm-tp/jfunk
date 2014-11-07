package com.mgmtp.jfunk.application.runner.exec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.launcher.CommandLauncher;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Executor that automatically uses the command interpreter when used on windows.
 *
 * @author rnaegele
 * @since 3.1.0
 */
public class OsDependentExecutor extends DefaultExecutor {

	/**
	 * Creates a process that runs a command. If used on Windows, the command is passed to "cmd /c".
	 *
	 * @param command
	 * 		the command to run
	 * @param env
	 * 		the environment for the command
	 * @param dir
	 * 		the working directory for the command
	 * @return the process started
	 * @throws java.io.IOException
	 * 		forwarded from the particular launcher used
	 */
	@Override
	protected Process launch(final CommandLine command, final Map env, final File dir) throws IOException {
		if (dir != null && !dir.exists()) {
			throw new IOException(dir + " doesn't exist.");
		}

		CommandLine cmdToExecute;
		if (OS.isFamilyWindows()) {
			cmdToExecute = new CommandLine("cmd");
			cmdToExecute.addArgument("/c");
			cmdToExecute.addArgument("\"", false);
			cmdToExecute.addArguments(command.toStrings());
			cmdToExecute.addArgument("\"", false);
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
			ProcessBuilder builder = new ProcessBuilder().directory(workingDir).command(command.toStrings());
			if (env != null) {
				builder.environment().putAll(env);
			}
			return builder.start();
		}

		@Override
		public boolean isFailure(final int exitValue) {
			return exitValue != 0;
		}
	}
}
