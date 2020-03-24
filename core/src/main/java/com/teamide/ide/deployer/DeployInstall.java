package com.teamide.ide.deployer;

import java.io.File;

import com.teamide.ide.shell.Shell;

public abstract class DeployInstall {

	public final DeployParam param;

	public final Shell shell;

	public DeployInstall(DeployParam param) {
		this.param = param;
		this.shell = getShell();
	}

	public abstract Shell getShell();

	public abstract void compile() throws Exception;

	public abstract File getServer() throws Exception;

	public abstract String getStartShell() throws Exception;

	public abstract String getStopShell() throws Exception;

	public abstract File getPIDFile() throws Exception;

	public abstract void copyProject() throws Exception;

}
