package com.teamide.starter.shell.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.shell.Shell;
import com.teamide.shell.java.JavaShell;
import com.teamide.starter.StarterParam;

public class JavaInternalStarterProcess extends JavaStarterProcess {

	protected final String internal_tomcat;

	public JavaInternalStarterProcess(StarterParam param) {
		super(param);

		this.internal_tomcat = param.starterJSON.getString("internal_tomcat");
	}

	@Override
	public Shell getShell() {
		JavaShell shell = new JavaShell(param.starterFolder);
		return shell;
	}

	@Override
	public String getStartShell() throws Exception {

		File appFolder = getAppFolder(param.starterServerFolder, null);

		String hostname = param.getOptionString("hostname");
		String port = param.getOptionString("port");
		String app = appFolder.getAbsolutePath();
		String contextpath = param.getOptionString("contextpath");

		JavaShell shell = (JavaShell) this.shell;
		shell.setJava_home(getJavaHome());
		shell.setJava_envp(getJavaEnvp());

		List<File> lib_folders = new ArrayList<File>();
		lib_folders.add(new File(param.starterServerFolder, "lib"));
		shell.setLib_folders(lib_folders);

		Map<String, String> envps = new HashMap<String, String>();
		envps.put("SERVER_ROOT", param.starterServerFolder.getAbsolutePath());
		envps.put("SERVER_PORT", port);
		envps.put("SERVER_HOSTNAME", hostname);
		envps.put("SERVER_CONTEXTPATH", contextpath);
		envps.put("SERVER_APP", app);
		shell.setEnvps(envps);

		shell.setMain("com.teamide.server.ServerMain");

		return shell.getShellString();
	}

	@Override
	public String getStopShell() throws Exception {
		return null;
	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

}
