package com.teamide.ide.deployer.install.java;

import java.io.File;

import org.apache.maven.shared.utils.io.FileUtils;

import com.teamide.ide.deployer.DeployParam;

public class JavaJarInstall extends JavaInstall {

	public JavaJarInstall(DeployParam param) {
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
		if (jarFile != null && jarFile.exists()) {
			File appJarFile = new File(param.starter.workFolder, jarFile.getName());
			if (appJarFile.exists()) {
				FileUtils.delete(appJarFile);
			}
			org.apache.commons.io.FileUtils.copyFile(jarFile, appJarFile);
		}
	}

}
