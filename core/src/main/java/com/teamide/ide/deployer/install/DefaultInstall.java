package com.teamide.ide.deployer.install;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import com.teamide.util.StringUtil;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.deployer.DeployInstall;
import com.teamide.ide.deployer.DeployParam;
import com.teamide.ide.shell.Shell;

public class DefaultInstall extends DeployInstall {

	public DefaultInstall(DeployParam param) {
		super(param);
	}

	@Override
	public Shell getShell() {
		return null;
	}

	public String formatCommand(String command) {
		if (StringUtil.isNotEmpty(command)) {
			File logFile = new File(param.starter.starterFolder, "log/starter.log");
			File pidFile = getPIDFile();
			if (logFile != null) {
				command = command.replaceAll("\\$STARTER_LOG_PATH", logFile.getAbsolutePath());
			}
			if (pidFile != null) {
				command = command.replaceAll("\\$STARTER_PID_PATH", pidFile.getAbsolutePath());
			}
			command = command.replaceAll("\\$PROJECT_PATH", this.param.projectFolder.getAbsolutePath());
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
		writer.println("cd " + this.param.projectFolder);
		writer.println(command);
		writer.flush();
		writer.close();
		InputStream inputStream = process.getInputStream();
		InputStream errorStream = process.getErrorStream();
		this.param.starter.read(inputStream, false);
		this.param.starter.read(errorStream, true);
		process.destroy();
	}

	@Override
	public File getPIDFile() {
		if (param.option != null && !StringUtil.isEmpty(param.option.getPidfile())) {
			return new File(param.option.getPidfile());
		}
		if (param.option != null && !StringUtil.isEmpty(param.option.getStartcommand())) {
			if (param.option.getStartcommand().indexOf("$STARTER_PID_PATH") >= 0) {
				return new File(param.starter.starterFolder, "starter.pid");
			}
		}
		return null;
	}

	@Override
	public void copyProject() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
