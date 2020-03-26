package com.teamide.ide.deployer.install.java;

import java.io.File;

import org.apache.maven.shared.utils.io.FileUtils;

import com.teamide.ide.deployer.DeployParam;

public class JavaMainInstall extends JavaInstall {

	public JavaMainInstall(DeployParam param) {
		super(param);
	}

	@Override
	public void compile() throws Exception {
		super.compile();

	}

	public String getMain() {
		return param.option.getMain();
	}

	@Override
	public File getServer() throws Exception {
		return null;
	}

	@Override
	public void copyProject() throws Exception {

		if (libFolder != null && libFolder.exists()) {
			File targetLibFolder = new File(param.starter.workFolder, "lib");
			if (targetLibFolder.exists()) {
				FileUtils.deleteDirectory(targetLibFolder);
			}
			org.apache.commons.io.FileUtils.copyDirectory(libFolder, targetLibFolder);
		}
		if (classesFolder != null && classesFolder.exists()) {

			File targetClassesFolder = new File(param.starter.workFolder, "classes");
			if (targetClassesFolder.exists()) {
				FileUtils.deleteDirectory(targetClassesFolder);
			}
			org.apache.commons.io.FileUtils.copyDirectory(classesFolder, targetClassesFolder);

		}

	}
}
