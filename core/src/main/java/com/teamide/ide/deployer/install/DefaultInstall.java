package com.teamide.ide.deployer.install;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

import com.teamide.util.StringUtil;
import com.teamide.ide.IDEConstant;
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

		FileUtils.deleteDirectory(this.param.starter.workFolder);

		FileUtils.copyDirectory(this.param.projectFolder, this.param.starter.workFolder, new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					if (pathname.getName().equals(".git")) {
						return false;
					} else if (pathname.getName().equals("node_modules")) {
						return false;
					}
				}
				return true;
			}
		});
	}

}
