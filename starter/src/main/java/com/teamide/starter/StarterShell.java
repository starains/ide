package com.teamide.starter;

import java.io.File;

import com.teamide.shell.Shell;

public abstract class StarterShell {

	protected final StarterParam param;

	protected final Shell shell;

	public StarterShell(StarterParam param) {
		this.param = param;
		this.shell = getShell();
	}

	public abstract Shell getShell();

	public String getSearchInfo() throws Exception {
		return this.param.token;
	}

	public abstract String getStartShell() throws Exception;

	public abstract String getStopShell() throws Exception;

	public abstract File getPIDFile() throws Exception;

	public abstract void copyWorkFolder() throws Exception;
}
