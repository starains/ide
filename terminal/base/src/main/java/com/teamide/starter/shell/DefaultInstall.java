package com.teamide.starter.shell;

import java.io.File;

import com.teamide.shell.Shell;
import com.teamide.starter.StarterParam;
import com.teamide.starter.StarterShell;
import com.teamide.util.StringUtil;

public class DefaultInstall extends StarterShell {

	public DefaultInstall(StarterParam param) {
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
			command = command.replaceAll("\\$WORK_PATH", param.workFolder.getAbsolutePath());
		}
		return command;
	}

	@Override
	public String getStartShell() {
		String command = param.getOptionString("startcommand");
		command = formatCommand(command);

		return command;
	}

	@Override
	public String getStopShell() {
		String command = param.getOptionString("stopcommand");
		command = formatCommand(command);

		return command;
	}

	@Override
	public File getPIDFile() {
		String pidfile = param.getOptionString("pidfile");
		if (StringUtil.isNotEmpty(pidfile)) {
			return new File(pidfile);
		}
		String startcommand = param.getOptionString("startcommand");
		if (StringUtil.isNotEmpty(startcommand)) {
			if (startcommand.indexOf("$STARTER_PID_PATH") >= 0) {
				return new File(param.starterFolder, "starter.pid");
			}
		}
		return null;
	}

}
