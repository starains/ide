package com.teamide.starter.shell.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.shell.java.JavaShell;
import com.teamide.starter.StarterParam;

public class JavaMainStarterShell extends JavaStarterShell {

	public JavaMainStarterShell(StarterParam param) {
		super(param);
	}

	@Override
	public String getStartShell() throws Exception {

		JavaShell shell = (JavaShell) this.shell;
		shell.setJava_home(getJavaHome());
		shell.setJava_envp(getJavaEnvp());
		List<File> lib_folders = new ArrayList<File>();
		lib_folders.add(new File(getWorkFolder(), "lib"));
		shell.setLib_folders(lib_folders);
		List<File> class_folders = new ArrayList<File>();
		class_folders.add(new File(getWorkFolder(), "classes"));
		shell.setClass_folders(class_folders);

		shell.setMain(getMain());

		Map<String, String> envps = new HashMap<String, String>();
		envps.put("STARTER_TOKEN", param.token);
		shell.setEnvps(envps);

		return shell.getShellString();
	}

	@Override
	public String getStopShell() throws Exception {
		return null;
	}

	public String getMain() {
		return option.getMain();
	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

}
