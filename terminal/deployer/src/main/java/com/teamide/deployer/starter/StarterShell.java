package com.teamide.deployer.starter;

import java.io.File;

import com.teamide.deployer.shell.Shell;

public abstract class StarterShell {

	protected final Starter starter;

	protected final Shell shell;

	public StarterShell(Starter starter) {
		this.starter = starter;
		this.shell = getShell();
	}

	public abstract Shell getShell();

	public abstract String getStartShell() throws Exception;

	public abstract String getStopShell() throws Exception;

	public abstract File getPIDFile() throws Exception;
}
