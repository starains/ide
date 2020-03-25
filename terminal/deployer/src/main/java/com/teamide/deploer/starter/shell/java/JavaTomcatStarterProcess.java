package com.teamide.deploer.starter.shell.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.deploer.shell.Shell;
import com.teamide.deploer.shell.java.JavaShell;
import com.teamide.deploer.starter.Starter;

public class JavaTomcatStarterProcess extends JavaStarterProcess {

	private final String tomcat_home;

	public JavaTomcatStarterProcess(Starter starter) {
		super(starter);

		this.tomcat_home = starter.starterJSON.getString("tomcat_home");

	}

	@Override
	public Shell getShell() {
		JavaShell shell = new JavaShell(starter.starterFolder);
		return shell;
	}

	@Override
	public String getStartShell() throws Exception {
		String tomcat_home = getTomcatHome();
		JavaShell shell = (JavaShell) this.shell;
		shell.setJava_home(getJavaHome());
		shell.setJava_envp(getJavaEnvp());

		Map<String, String> envps = new HashMap<String, String>();
		envps.put("catalina.base", tomcat_home);
		envps.put("catalina.home", tomcat_home);
		envps.put("wtp.deploy", tomcat_home + "webapps");
		envps.put("java.endorsed.dirs", tomcat_home + "endorsed");
		envps.put("java.library.path", tomcat_home + "bin");
		shell.setEnvps(envps);

		List<File> lib_folders = new ArrayList<File>();
		lib_folders.add(new File(tomcat_home + "lib"));
		lib_folders.add(new File(tomcat_home + "bin"));
		lib_folders.add(new File(getJavaHome() + "lib"));
		shell.setLib_folders(lib_folders);

		shell.setMain("org.apache.catalina.startup.Bootstrap");

		return shell.getShellString();
	}

	@Override
	public String getStopShell() throws Exception {
		return null;
	}

	public String getTomcatHome() {
		return tomcat_home;
	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

}
