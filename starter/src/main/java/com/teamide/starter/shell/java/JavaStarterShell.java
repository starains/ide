package com.teamide.starter.shell.java;

import com.teamide.starter.StarterParam;
import com.teamide.starter.StarterShell;

public abstract class JavaStarterShell extends StarterShell {

	private final String java_home;

	private final String maven_home;

	public JavaStarterShell(StarterParam param) {
		super(param);

		this.java_home = param.starterJSON.getString("java_home");
		this.maven_home = param.starterJSON.getString("maven_home");
	}

	public String getJavaEnvp() {
		return param.getOptionString("javaenvp");
	}

	public String getMavenEnvp() {
		return param.getOptionString("mavenenvp");
	}

	public String getContextpath() {
		return param.getOptionString("contextpath");
	}

	public String getJavaHome() {
		return java_home;
	}

	public String getMavenHome() {
		return maven_home;
	}

}
