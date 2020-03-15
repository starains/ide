package com.teamide.ide.processor.repository.starter;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import com.teamide.util.StringUtil;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.shell.Shell;

public class DefaultStarterProcess extends StarterProcess {

	public DefaultStarterProcess(StarterParam param) {
		super(param);
	}

	@Override
	public Shell getShell() {
		return null;
	}

	public String formatCommand(String command) {
		if (StringUtil.isNotEmpty(command)) {
			File logFile = new File(param.starterFolder, "log/starter.log");
			File pidFile = getPIDFile();
			if (logFile != null) {
				command = command.replaceAll("\\$STARTER_LOG_PATH", logFile.getAbsolutePath());
			}
			if (pidFile != null) {
				command = command.replaceAll("\\$STARTER_PID_PATH", pidFile.getAbsolutePath());
			}
			command = command.replaceAll("\\$SOURCE_PATH", param.param.getSourceFolder().getAbsolutePath());
		}
		return command;
	}

	@Override
	public String getStartShell() {
		if (param.option != null) {
			String command = param.option.getStartcommand();
			command = formatCommand(command);

			return command;
		}
		return null;
	}

	@Override
	public String getStopShell() {
		if (param.option != null) {
			String command = param.option.getStopcommand();
			command = formatCommand(command);
		}
		return null;
	}

	@Override
	public File getServer() {
		return null;
	}

	@Override
	public File getWorkFolder() {
		return this.param.param.getSourceFolder();
	}

	@Override
	public void compile() throws Exception {
		String command = null;
		if (param.option != null) {
			command = param.option.getCompilecommand();
		}
		if (StringUtil.isEmpty(command)) {
			return;
		}
		String[] cmd = null;
		if (IDEConstant.IS_OS_WINDOW) {
			cmd = new String[] { "cmd" };
		} else {
			cmd = new String[] { "/bin/sh" };
		}
		Process process = Runtime.getRuntime().exec(cmd);

		PrintWriter writer = new PrintWriter(process.getOutputStream());
		writer.println("cd " + this.param.param.getSourceFolder());
		writer.println(command);
		writer.flush();
		writer.close();
		InputStream inputStream = process.getInputStream();
		InputStream errorStream = process.getErrorStream();
		this.param.read(inputStream, false);
		this.param.read(errorStream, true);
		process.destroy();
	}

	@Override
	public File getPIDFile() {
		if (param.option != null && !StringUtil.isEmpty(param.option.getPidfile())) {
			return new File(param.option.getPidfile());
		}
		if (param.option != null && !StringUtil.isEmpty(param.option.getStartcommand())) {
			if (param.option.getStartcommand().indexOf("$STARTER_PID_PATH") >= 0) {
				return new File(param.starterFolder, "starter.pid");
			}
		}
		return null;
	}

}
