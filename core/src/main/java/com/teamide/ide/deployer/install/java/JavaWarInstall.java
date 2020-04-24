package com.teamide.ide.deployer.install.java;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.teamide.ide.deployer.DeployParam;

public class JavaWarInstall extends JavaInstall {

	public JavaWarInstall(DeployParam param) {
		super(param);
	}

	public File getJarFile() {

		return null;
	}

	@Override
	public File getServer() throws Exception {
		return null;
	}

	@Override
	public void compile() throws Exception {
		super.compile();
		readyRemoteWorkFolder();
	}

	@Override
	public void readyRemoteWorkFolder() throws Exception {
		if (warFile != null && warFile.exists()) {
			File appWarFile = new File(getRemoteWorkFolder(), warFile.getName());
			if (appWarFile.exists()) {
				FileUtils.forceDelete(appWarFile);
			}
			FileUtils.copyFile(warFile, appWarFile);
		}
	}

}
