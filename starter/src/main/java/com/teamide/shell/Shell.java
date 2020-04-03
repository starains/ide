package com.teamide.shell;

import java.io.File;
import java.util.List;

public abstract class Shell {

	public static final boolean IS_OS_WINDOW = isOSWindow();

	private static boolean isOSWindow() {

		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("window") > -1) {
			return true;
		} else {
			return false;
		}
	}

	protected final File starterFolder;

	public Shell(File starterFolder) {
		this.starterFolder = starterFolder;
	}

	protected abstract List<String> getWindowShell();

	protected abstract List<String> getLinuxShell();

	public List<String> getShell() throws Exception {
		if (IS_OS_WINDOW) {
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
		if (IS_OS_WINDOW) {
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
