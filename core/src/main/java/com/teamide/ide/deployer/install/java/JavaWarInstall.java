package com.teamide.ide.deployer.install.java;

import java.io.File;

import org.apache.maven.shared.utils.io.FileUtils;

import com.teamide.ide.deployer.DeployParam;

public class JavaWarInstall extends JavaInstall {

	public JavaWarInstall(DeployParam param) {
		super(param);
	}

	@Override
	public void compile() throws Exception {
		super.compile();

	}

	public File getJarFile() {

		return null;
	}

	@Override
	public File getServer() throws Exception {
		return null;
	}

	@Override
	public void copyProject() throws Exception {
		if (warFile != null && warFile.exists()) {
			File appWarFile = new File(param.starter.workFolder, warFile.getName());
			if (appWarFile.exists()) {
				FileUtils.delete(appWarFile);
			}
			org.apache.commons.io.FileUtils.copyFile(warFile, appWarFile);
		}
	}

}
