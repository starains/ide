package com.teamide.ide.deployer;

import java.io.File;

import com.teamide.deploer.starter.Starter;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.bean.StarterOption;
import com.teamide.ide.deployer.install.DefaultInstall;
import com.teamide.ide.deployer.install.java.JavaInternalStarterProcess;
import com.teamide.ide.deployer.install.java.JavaJarStarterProcess;
import com.teamide.ide.deployer.install.java.JavaMainStarterProcess;
import com.teamide.ide.deployer.install.java.JavaTomcatStarterProcess;
import com.teamide.ide.deployer.install.node.NodeStarterProcess;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.service.impl.EnvironmentService;
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
			RepositoryProcessorParam param = new RepositoryProcessorParam(null, spaceid, branch);
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
				install = new JavaMainStarterProcess(this);
				break;
			case "JAR":
				install = new JavaJarStarterProcess(this);
				break;
			case "TOMCAT":
				if (option.isUseinternal()) {
					install = new JavaInternalStarterProcess(this);
				} else {
					install = new JavaTomcatStarterProcess(this);
				}
				break;
			}
			break;
		case "NODE":
			install = new NodeStarterProcess(this);
			break;
		}
		if (install == null) {
			install = new DefaultInstall(this);
		}
		return install;
	}
}
