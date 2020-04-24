package com.teamide.starter.shell;

import java.io.File;

import com.teamide.shell.Shell;
import com.teamide.starter.StarterParam;
import com.teamide.starter.StarterShell;
import com.teamide.starter.bean.OtherOptionBean;
import com.teamide.util.StringUtil;

public class DefaultStarterShell extends StarterShell {

	protected final OtherOptionBean option;

	public DefaultStarterShell(StarterParam param) {
		super(param);
		this.option = (OtherOptionBean) param.option;
	}

	@Override
	public Shell getShell() {
		return null;
	}

	public String formatCommand(String command) throws Exception {
		if (StringUtil.isNotEmpty(command)) {
			File logFile = new File(param.starterFolder, "log/starter.log");
			File pidFile = getPIDFile();
			if (logFile != null) {
				command = command.replaceAll("\\$STARTER_LOG_PATH", logFile.getAbsolutePath());
			}
			if (pidFile != null) {
				command = command.replaceAll("\\$STARTER_PID_PATH", pidFile.getAbsolutePath());
			}
			command = command.replaceAll("\\$WORK_PATH", getWorkFolder().getAbsolutePath());
		}
		return command;
	}

	@Override
	public String getStartShell() throws Exception {
		String command = option.getStartcommand();
		command = formatCommand(command);

		return command;
	}

	@Override
	public String getStopShell() throws Exception {
		String command = option.getStopcommand();
		command = formatCommand(command);

		return command;
	}

	@Override
	public File getPIDFile() throws Exception {
		String pidfile = option.getPidfile();
		if (StringUtil.isNotEmpty(pidfile)) {
			return new File(pidfile);
		}
		String startcommand = option.getStartcommand();
		if (StringUtil.isNotEmpty(startcommand)) {
			if (startcommand.indexOf("$STARTER_PID_PATH") >= 0) {
				return new File(param.starterFolder, "starter.pid");
			}
		}
		return null;
	}

	@Override
	public void startReady() throws Exception {

	}

	@Override
	public File getWorkFolder() throws Exception {
		return new File(this.option.getPath());
	}

}
