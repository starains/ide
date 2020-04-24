package com.teamide.ide.deployer.install.java;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.teamide.ide.deployer.DeployParam;

public class JavaMainInstall extends JavaInstall {

	public JavaMainInstall(DeployParam param) {
		super(param);
	}

	public String getMain() {
		return option.getMain();
	}

	@Override
	public File getServer() throws Exception {
		return null;
	}

	@Override
	public void readyRemoteWorkFolder() throws Exception {

		if (libFolder != null && libFolder.exists()) {
			File targetLibFolder = new File(getRemoteWorkFolder(), "lib");
			if (targetLibFolder.exists()) {
				FileUtils.deleteDirectory(targetLibFolder);
			}
			org.apache.commons.io.FileUtils.copyDirectory(libFolder, targetLibFolder);
		}
		if (classesFolder != null && classesFolder.exists()) {

			File targetClassesFolder = new File(getRemoteWorkFolder(), "classes");
			if (targetClassesFolder.exists()) {
				FileUtils.deleteDirectory(targetClassesFolder);
			}
			org.apache.commons.io.FileUtils.copyDirectory(classesFolder, targetClassesFolder);

		}

	}
}
