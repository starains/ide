package com.teamide.ide.deployer.install.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.teamide.ide.deployer.DeployParam;
import com.teamide.ide.shell.Shell;
import com.teamide.ide.shell.java.JavaShell;

public class JavaJarStarterProcess extends JavaStarterProcess {

	public JavaJarStarterProcess(DeployParam param) {
		super(param);
	}

	@Override
	public Shell getShell() {
		JavaShell shell = new JavaShell(param.starter.starterFolder);
		return shell;
	}

	@Override
	public String getStartShell() throws Exception {

		JavaShell shell = (JavaShell) this.shell;
		shell.setJava_home(getJavaHome());
		shell.setJava_envp(getJavaEnvp());

		List<File> lib_folders = new ArrayList<File>();
		lib_folders.add(new File(param.starter.workFolder, "lib"));
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
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

	@Override
	public void copyProject() throws Exception {

	}

}
