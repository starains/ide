package com.teamide.deployer.starter.shell.java;

import java.io.File;

import com.teamide.deployer.starter.Starter;
import com.teamide.deployer.starter.StarterShell;
import com.teamide.util.StringUtil;

public abstract class JavaStarterProcess extends StarterShell {

	private final String java_home;

	private final String maven_home;

	public JavaStarterProcess(Starter starter) {
		super(starter);

		this.java_home = starter.starterJSON.getString("java_home");
		this.maven_home = starter.starterJSON.getString("maven_home");
	}

	public String getJavaEnvp() {
		return starter.getOptionString("javaenvp");
	}

	public String getMavenEnvp() {
		return starter.getOptionString("mavenenvp");
	}

	public String getContextpath() {
		return starter.getOptionString("contextpath");
	}

	public String getJavaHome() {
		return java_home;
	}

	public String getMavenHome() {
		return maven_home;
	}

	public File getAppFolder(File tomcatFolder, String name) {
		File webappsFolder = new File(tomcatFolder, "webapps");
		String contextpath = getContextpath();
		if (StringUtil.isEmpty(contextpath)) {
			contextpath = name;
		}
		String outName = contextpath;
		if (StringUtil.isEmpty(outName) || outName.equals("/")) {
			outName = "/ROOT";
		}
		File appFolder = new File(webappsFolder, outName);

		return appFolder;
	}

	// protected void outWebapps(File tomcatFolder) throws Exception {
	// File targetWebappFolder = new File(param.starter.workFolder, "webapp");
	// if (targetWebappFolder.exists() && targetWebappFolder.listFiles().length
	// > 0) {
	// targetWebappFolder = targetWebappFolder.listFiles()[0];
	// }
	// if (targetWebappFolder != null && targetWebappFolder.exists()) {
	// if (tomcatFolder == null || !tomcatFolder.exists()) {
	// throw new Exception("tomcat[" + tomcatFolder.getAbsolutePath() + "] does
	// not exist.");
	// }
	//
	// File appFolder = getAppFolder(tomcatFolder,
	// targetWebappFolder.getName());
	//
	// if (appFolder.exists()) {
	// try {
	// FileUtils.deleteDirectory(appFolder);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// if (!appFolder.exists()) {
	// appFolder.mkdirs();
	// }
	// try {
	// FileUtils.copyDirectoryStructure(targetWebappFolder, appFolder);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// }

}
