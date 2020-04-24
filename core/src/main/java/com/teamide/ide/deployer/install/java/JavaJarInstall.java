package com.teamide.ide.deployer.install.java;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.teamide.ide.deployer.DeployParam;

public class JavaJarInstall extends JavaInstall {

	public JavaJarInstall(DeployParam param) {
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
	public void readyRemoteWorkFolder() throws Exception {
		if (jarFile != null && jarFile.exists()) {
			File appJarFile = new File(getRemoteWorkFolder(), jarFile.getName());
			if (appJarFile.exists()) {
				FileUtils.forceDelete(appJarFile);
			}
			FileUtils.copyFile(jarFile, appJarFile);
		}
	}

}
