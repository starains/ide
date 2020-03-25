package com.teamide.ide.deployer.install;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import com.teamide.util.StringUtil;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.deployer.DeployInstall;
import com.teamide.ide.deployer.DeployParam;

public class DefaultInstall extends DeployInstall {

	public DefaultInstall(DeployParam param) {
		super(param);
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
	public void copyProject() throws Exception {
		// TODO Auto-generated method stub

	}

}
