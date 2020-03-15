package com.teamide.ide.processor.repository.starter;

import java.io.File;

import com.teamide.ide.processor.repository.starter.StarterParam;
import com.teamide.ide.shell.Shell;

public abstract class StarterProcess {

	public final StarterParam param;

	public final Shell shell;

	public StarterProcess(StarterParam param) {
		this.param = param;
		this.shell = getShell();
	}

	public abstract Shell getShell();

	public abstract void compile() throws Exception;

	public abstract File getServer() throws Exception;

	public abstract File getWorkFolder() throws Exception;

	public abstract String getStartShell() throws Exception;

	public abstract String getStopShell() throws Exception;

	public abstract File getPIDFile() throws Exception;

}
