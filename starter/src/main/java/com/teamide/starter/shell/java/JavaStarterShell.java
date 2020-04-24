package com.teamide.starter.shell.java;

import java.io.File;

import com.teamide.starter.StarterParam;
import com.teamide.starter.StarterShell;
import com.teamide.starter.bean.JavaOptionBean;
import com.teamide.util.StringUtil;

public abstract class JavaStarterShell extends StarterShell {

	protected final JavaOptionBean option;

	public JavaStarterShell(StarterParam param) {
		super(param);
		this.option = (JavaOptionBean) param.option;
	}

	public String getJavaEnvp() {
		return option.getJavaenvp();
	}

	public String getMavenEnvp() {
		return option.getMavenenvp();
	}

	public String getContextpath() {
		return option.getContextpath();
	}

	public String getJavaHome() {
		return option.getJava_home();
	}

	public String getMavenHome() {
		return option.getMaven_home();
	}

	@Override
	public void startReady() throws Exception {

	}

	@Override
	public File getWorkFolder() throws Exception {
		if (StringUtil.isEmpty(option.getRemoteid())) {
			return new File(this.option.getPath(), "target");
		} else {
			return new File(this.param.starterFolder, "work");
		}
	}
}
