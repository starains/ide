package com.teamide.deployer.starter.shell.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.teamide.deployer.shell.Shell;
import com.teamide.deployer.shell.java.JavaShell;
import com.teamide.deployer.starter.Starter;

public class JavaMainStarterProcess extends JavaStarterProcess {

	public JavaMainStarterProcess(Starter starter) {
		super(starter);
	}

	@Override
	public Shell getShell() {
		JavaShell shell = new JavaShell(starter.starterFolder);
		return shell;
	}

	@Override
	public String getStartShell() throws Exception {

		JavaShell shell = (JavaShell) this.shell;
		shell.setJava_home(getJavaHome());
		shell.setJava_envp(getJavaEnvp());
		List<File> lib_folders = new ArrayList<File>();
		lib_folders.add(new File(starter.workFolder, "lib"));
		shell.setLib_folders(lib_folders);
		List<File> class_folders = new ArrayList<File>();
		class_folders.add(new File(starter.workFolder, "classes"));
		shell.setClass_folders(class_folders);

		shell.setMain(getMain());

		return shell.getShellString();
	}

	@Override
	public String getStopShell() throws Exception {
		return null;
	}

	public String getMain() {
		return starter.getOptionString("main");
	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

}
