package com.teamide.protect.ide.processor.repository.starter.java;

import java.io.File;

import com.teamide.protect.ide.processor.repository.starter.StarterParam;
import com.teamide.protect.ide.shell.Shell;
import com.teamide.protect.ide.shell.java.JavaShell;

public class JavaJarStarterProcess extends JavaStarterProcess {

	public JavaJarStarterProcess(StarterParam param) {
		super(param);
	}

	@Override
	public Shell getShell() {
		JavaShell shell = new JavaShell(param.starterFolder);
		return shell;
	}

	@Override
	public String getStartShell() throws Exception {

		JavaShell shell = (JavaShell) this.shell;
		shell.setJava_home(getJavaHome());
		shell.setJava_envp(getJavaEnvp());

		shell.setLib_folders(lib_folders);
		shell.setJar_file(getJarFile());

		return shell.getShellString();
	}

	@Override
	public String getStopShell() throws Exception {
		return null;
	}

	@Override
	public void compile() throws Exception {
		super.compile();

	}

	public File getJarFile() {
		return null;
	}

	@Override
	public File getServer() throws Exception {
		return null;
	}

	@Override
	public File getWorkFolder() throws Exception {
		return this.param.param.getSourceFolder();
	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

}
