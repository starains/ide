package com.teamide.deployer.starter.shell.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.teamide.deployer.shell.Shell;
import com.teamide.deployer.shell.java.JavaShell;
import com.teamide.deployer.starter.Starter;

public class JavaJarStarterProcess extends JavaStarterProcess {

	public JavaJarStarterProcess(Starter starter) {
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
		shell.setJar_file(new File(starter.workFolder, "app.jar"));

		return shell.getShellString();
	}

	@Override
	public String getStopShell() throws Exception {
		return null;
	}

	public File getJarFile() {

		return null;
	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

}
