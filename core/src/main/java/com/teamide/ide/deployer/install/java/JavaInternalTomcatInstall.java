package com.teamide.ide.deployer.install.java;

import java.io.File;
import java.io.IOException;

import org.apache.maven.shared.utils.io.FileUtils;

import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.deployer.DeployParam;
import com.teamide.util.FileUtil;

public class JavaInternalTomcatInstall extends JavaInstall {

	public JavaInternalTomcatInstall(DeployParam param) {
		super(param);

		try {
			param.starter.starterJSON.put("internal_tomcat", param.option.getInternaltomcat());
			FileUtil.write(param.starter.starterJSON.toJSONString().getBytes(), param.starter.starterJSONFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public File getServer() throws Exception {

		File tomcat_folder = new File(IDEConstant.PLUGINS_SERVER_FOLDER, param.option.getInternaltomcat());
		if (!tomcat_folder.exists()) {
			throw new Exception(param.option.getInternaltomcat() + " is not defind");
		}
		return tomcat_folder;
	}

	@Override
	public void copyProject() throws Exception {

		if (warFolder != null && warFolder.exists()) {
			File targetWebappFolder = new File(param.starter.workFolder, "webapp");
			if (targetWebappFolder.exists()) {
				FileUtils.deleteDirectory(targetWebappFolder);
			}
			org.apache.commons.io.FileUtils.copyDirectory(warFolder, new File(targetWebappFolder, warFolder.getName()));
		}
	}
}
