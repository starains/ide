package com.teamide.deploer;

public class IDEConstant {

	public static final String HOME = getHome();

	public static final boolean IS_OS_LINUX = isOSLinux();

	public static final boolean IS_OS_WINDOW = isOSWindow();

	public static final String BIN_FOLDER = HOME + "bin/";

	public static final String CONF_FOLDER = HOME + "conf/";

	public static final String CONF = CONF_FOLDER + "config.properties";

	public static final String UPDATE_FOLDER = HOME + "update/";

	public static final String WORK_FOLDER = HOME + "work/";

	public static final String RUNNER_FOLDER = WORK_FOLDER + "runner/";

	public static final String TOMCAT_FOLDER = WORK_FOLDER + "tomcat/";

	public static final String TOMCAT_WORK_FOLDER = TOMCAT_FOLDER + "work/";

	public static final String TOMCAT_WEBAPPS_FOLDER = TOMCAT_FOLDER + "webapps/";

	private static final String getHome() {

		String home = System.getenv("COOS_RUNNER_HOME");
		if (home == null || home.trim().length() == 0) {
			home = System.getProperty("COOS_RUNNER_HOME");
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

		System.setProperty("COOS_RUNNER_HOME", home);
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

	private static boolean isOSWindow() {

		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("window") > -1) {
			return true;
		} else {
			return false;
		}
	}
}
