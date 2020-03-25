package com.teamide.deployer.starter.shell;

import java.io.File;

import com.teamide.deployer.shell.Shell;
import com.teamide.deployer.starter.Starter;
import com.teamide.deployer.starter.StarterShell;
import com.teamide.util.StringUtil;

public class DefaultInstall extends StarterShell {

	public DefaultInstall(Starter starter) {
		super(starter);
	}

	@Override
	public Shell getShell() {
		return null;
	}

	public String formatCommand(String command) {
		if (StringUtil.isNotEmpty(command)) {
			File logFile = new File(starter.starterFolder, "log/starter.log");
			File pidFile = getPIDFile();
			if (logFile != null) {
				command = command.replaceAll("\\$STARTER_LOG_PATH", logFile.getAbsolutePath());
			}
			if (pidFile != null) {
				command = command.replaceAll("\\$STARTER_PID_PATH", pidFile.getAbsolutePath());
			}
			command = command.replaceAll("\\$WORK_PATH", starter.workFolder.getAbsolutePath());
		}
		return command;
	}

	@Override
	public String getStartShell() {
		String command = starter.getOptionString("startcommand");
		command = formatCommand(command);

		return command;
	}

	@Override
	public String getStopShell() {
		String command = starter.getOptionString("stopcommand");
		command = formatCommand(command);

		return command;
	}

	@Override
	public File getPIDFile() {
		String pidfile = starter.getOptionString("pidfile");
		if (StringUtil.isNotEmpty(pidfile)) {
			return new File(pidfile);
		}
		String startcommand = starter.getOptionString("startcommand");
		if (StringUtil.isNotEmpty(startcommand)) {
			if (startcommand.indexOf("$STARTER_PID_PATH") >= 0) {
				return new File(starter.starterFolder, "starter.pid");
			}
		}
		return null;
	}

}
