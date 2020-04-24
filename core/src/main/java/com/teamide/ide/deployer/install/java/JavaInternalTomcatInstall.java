package com.teamide.ide.deployer.install.java;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.deployer.DeployParam;

public class JavaInternalTomcatInstall extends JavaInstall {

	public JavaInternalTomcatInstall(DeployParam param) {
		super(param);
	}

	@Override
	public File getServer() throws Exception {

		File tomcat_folder = new File(IDEConstant.PLUGINS_TOMCAT_FOLDER, option.getInternaltomcat());
		if (!tomcat_folder.exists()) {
			throw new Exception(option.getInternaltomcat() + " is not defind");
		}
		return tomcat_folder;
	}

	@Override
	public void compile() throws Exception {
		super.compile();
		readyRemoteWorkFolder();
	}

	@Override
	public void readyRemoteWorkFolder() throws Exception {

		if (warFolder != null && warFolder.exists()) {
			File targetWebappFolder = new File(getRemoteWorkFolder(), "webapp");
			if (targetWebappFolder.exists()) {
				FileUtils.deleteDirectory(targetWebappFolder);
			}
			FileUtils.copyDirectory(warFolder, new File(targetWebappFolder, warFolder.getName()));
		}
	}
}
