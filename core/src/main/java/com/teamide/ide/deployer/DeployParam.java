package com.teamide.ide.deployer;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.StarterOption;
import com.teamide.ide.deployer.install.DefaultInstall;
import com.teamide.ide.deployer.install.java.JavaInternalTomcatInstall;
import com.teamide.ide.deployer.install.java.JavaJarInstall;
import com.teamide.ide.deployer.install.java.JavaMainInstall;
import com.teamide.ide.deployer.install.java.JavaWarInstall;
import com.teamide.ide.deployer.install.node.NodeInstall;
import com.teamide.ide.handler.SpaceHandler;
import com.teamide.ide.param.RepositoryProcessorParam;
import com.teamide.ide.service.impl.EnvironmentService;
import com.teamide.starter.Starter;
import com.teamide.util.StringUtil;

public class DeployParam {

	public final Starter starter;

	public final String spaceid;

	public final String branch;

	public final EnvironmentBean environment;

	public final DeployInstall deployInstall;

	public final File projectFolder;

	public final StarterOption option;

	public DeployParam(File starterFolder) {
		this.starter = new Starter(starterFolder);

		this.spaceid = this.starter.starterJSON.getString("spaceid");
		this.branch = this.starter.starterJSON.getString("branch");
		String path = this.starter.starterJSON.getString("path");
		File projectFolder = null;
		if (!StringUtil.isEmpty(spaceid)) {
			SpaceBean space = SpaceHandler.get(spaceid);
			JSONObject formatSpace = SpaceHandler.getFormat(space);
			RepositoryProcessorParam param = new RepositoryProcessorParam(null, spaceid, formatSpace, branch);
			projectFolder = param.getFile(path);
		}
		this.projectFolder = projectFolder;

		StarterOption option = null;
		if (this.starter.starterJSON.getJSONObject("option") != null) {
			option = this.starter.starterJSON.getJSONObject("option").toJavaObject(StarterOption.class);
		}
		this.option = option;

		EnvironmentBean environment = null;
		if (this.option != null) {
			String environmentid = this.option.getEnvironmentid();
			if (!StringUtil.isEmpty(environmentid)) {
				try {
					environment = new EnvironmentService().get(environmentid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.environment = environment;

		this.deployInstall = createDeployInstall();

	}

	private DeployInstall createDeployInstall() {
		if (this.option == null) {
			return null;
		}
		DeployInstall install = null;
		String language = option.getLanguage();
		switch (String.valueOf(language)) {
		case "JAVA":
			String mode = option.getMode();
			switch (String.valueOf(mode)) {
			case "MAIN":
				install = new JavaMainInstall(this);
				break;
			case "JAR":
				install = new JavaJarInstall(this);
				break;
			case "WAR":
				install = new JavaWarInstall(this);
				break;
			case "TOMCAT":
				install = new JavaInternalTomcatInstall(this);
				break;
			}
			break;
		case "NODE":
			install = new NodeInstall(this);
			break;
		}
		if (install == null) {
			install = new DefaultInstall(this);
		}
		return install;
	}

	public File getPIDFile() {
		if (option != null && !StringUtil.isEmpty(option.getPidfile())) {
			return new File(option.getPidfile());
		}
		if (option != null && !StringUtil.isEmpty(option.getStartcommand())) {
			if (option.getStartcommand().indexOf("$STARTER_PID_PATH") >= 0) {
				return new File(starter.starterFolder, "starter.pid");
			}
		}
		return new File(starter.starterFolder, "starter.pid");
	}
}
