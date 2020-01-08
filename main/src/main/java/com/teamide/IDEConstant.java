package com.teamide;

public class IDEConstant {

	public static final String HOME = getHome();

	public static final boolean IS_OS_LINUX = isOSLinux();

	public static final String BIN_FOLDER = HOME + "bin/";

	public static final String WEBAPPS_FOLDER = HOME + "webapps/";

	public static final String UPDATE_FOLDER = HOME + "update/";

	public static final String IDE_JAR = WEBAPPS_FOLDER + "ide.jar";

	private static final String getHome() {

		String home = System.getenv("COOS_HOME");
		if (home == null || home.trim().length() == 0) {
			home = System.getProperty("COOS_HOME");
		}
		if (home == null || home.trim().length() == 0) {
			home = System.getProperty("user.dir");
			home = home.replaceAll("\\\\", "/");
			int index = home.lastIndexOf("/bin");
			if (index >= 0) {
				home = home.substring(0, index);
			}
			index = home.lastIndexOf("/lib");
			if (index >= 0) {
				home = home.substring(0, index);
			}
		} else {
			home = home.replaceAll("\\\\", "/");
		}
		if (!home.endsWith("/")) {
			home = home + "/";
		}
		System.setProperty("COOS_HOME", home);
		return home;
	}

	private static boolean isOSLinux() {

		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			return true;
		} else {
			return false;
		}
	}
}
