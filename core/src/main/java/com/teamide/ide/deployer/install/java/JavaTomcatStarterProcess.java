package com.teamide.ide.deployer.install.java;

import java.io.File;
import java.io.IOException;

import org.apache.maven.shared.utils.io.FileUtils;

import com.teamide.ide.deployer.DeployParam;
import com.teamide.util.FileUtil;

public class JavaTomcatStarterProcess extends JavaStarterProcess {

	private final String tomcat_home;

	public JavaTomcatStarterProcess(DeployParam param) {
		super(param);
		String tomcat_home = null;
		if (param.environment != null) {
			tomcat_home = param.starter.formatToRoot(param.environment.getPath());
		}

		this.tomcat_home = tomcat_home;

		try {
			param.starter.starterJSON.put("tomcat_home", tomcat_home);
			FileUtil.write(param.starter.starterJSON.toJSONString().getBytes(), param.starter.starterJSONFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void compile() throws Exception {
		String tomcat_home = getTomcatHome();
		if (tomcat_home == null) {
			throw new Exception("未配置tomcat");
		}
		super.compile();

	}

	@Override
	public File getServer() throws Exception {
		return null;
	}

	public String getTomcatHome() {
		return tomcat_home;
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
