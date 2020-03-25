package com.teamide.deployer.shell;

import java.io.File;
import java.util.List;

import com.teamide.ide.constant.IDEConstant;

public abstract class Shell {

	protected final File starterFolder;

	public Shell(File starterFolder) {
		this.starterFolder = starterFolder;
	}

	protected abstract List<String> getWindowShell();

	protected abstract List<String> getLinuxShell();

	public List<String> getShell() throws Exception {
		if (IDEConstant.IS_OS_WINDOW) {
			return getWindowShell();
		} else {
			return getLinuxShell();
		}
	}

	public String getShellString() throws Exception {
		List<String> shell = getShell();
		StringBuffer buffer = new StringBuffer();
		for (String line : shell) {
			buffer.append(line).append("\n");
		}
		return buffer.toString();
	}

	public File getPIDFile() {
		if (IDEConstant.IS_OS_WINDOW) {
			return null;
		}
		if (starterFolder == null) {
			return null;
		}

		return new File(starterFolder, "starter.pid");
	}

	public File getStarterFolder() {
		return starterFolder;
	}

}
