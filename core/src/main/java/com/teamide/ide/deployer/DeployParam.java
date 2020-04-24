package com.teamide.ide.deployer;

import java.io.File;

import com.teamide.ide.deployer.install.DefaultInstall;
import com.teamide.ide.deployer.install.java.JavaInternalTomcatInstall;
import com.teamide.ide.deployer.install.java.JavaJarInstall;
import com.teamide.ide.deployer.install.java.JavaMainInstall;
import com.teamide.ide.deployer.install.java.JavaWarInstall;
import com.teamide.starter.Starter;
import com.teamide.starter.bean.JavaOptionBean;
import com.teamide.util.StringUtil;

public class DeployParam {

	public final Starter starter;

	public final DeployInstall deployInstall;

	public final File codeFolder;

	public DeployParam(File starterFolder) {
		this.starter = new Starter(starterFolder);

		if (StringUtil.isEmpty(this.starter.option.getPath())) {
			throw new RuntimeException(starterFolder + " starter code path is null.");
		}
		this.codeFolder = new File(this.starter.option.getPath());

		this.deployInstall = createDeployInstall();

	}

	private DeployInstall createDeployInstall() {
		if (this.starter.option == null) {
			return null;
		}
		DeployInstall install = null;
		if (this.starter.option instanceof JavaOptionBean) {
			JavaOptionBean javaOption = (JavaOptionBean) this.starter.option;
			String mode = javaOption.getMode();
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
		}
		if (install == null) {
			install = new DefaultInstall(this);
		}
		return install;
	}

	// public File getPIDFile() {
	// if (option != null && !StringUtil.isEmpty(option.getPidfile())) {
	// return new File(option.getPidfile());
	// }
	// if (option != null && !StringUtil.isEmpty(option.getStartcommand())) {
	// if (option.getStartcommand().indexOf("$STARTER_PID_PATH") >= 0) {
	// return new File(starter.starterFolder, "starter.pid");
	// }
	// }
	// return new File(starter.starterFolder, "starter.pid");
	// }
}
