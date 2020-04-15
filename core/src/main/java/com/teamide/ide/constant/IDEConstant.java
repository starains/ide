package com.teamide.ide.constant;

import java.io.File;

public class IDEConstant {

	public static final String HOME = getHome();

	public static final boolean IS_OS_LINUX = isOSLinux();

	public static final boolean IS_OS_WINDOW = isOSWindow();

	public static final String CONF_FOLDER = HOME + "conf/";

	public static final String PLUGINS_FOLDER = HOME + "plugins/";

	public static final String PLUGINS_TOMCAT_FOLDER = PLUGINS_FOLDER + "tomcat/";

	public static final String WORKSPACES_FOLDER = HOME + "workspaces/";

	public static final String WORKSPACES_TOMCAT_FOLDER = WORKSPACES_FOLDER + "tomcat/";

	public static final String WORKSPACES_STARTER_FOLDER = WORKSPACES_FOLDER + "starter/";

	public static final String WORKSPACES_TEMP_FOLDER = WORKSPACES_FOLDER + "temp/";

	public static final String SPACE_FOLDER = HOME + "space/";

	public static final String JDBC = CONF_FOLDER + "jdbc.properties";

	public static final String CONFIG = CONF_FOLDER + "ide.conf";

	public static final IDEConf CONF = IDEConf.load(new File(CONFIG));

	public static final String PUBLIC = "public";

	private static final String getHome() {

		String home = System.getProperty("TEAMIDE_HOME");
		if (home == null || home.trim().length() == 0) {
			home = System.getenv("TEAMIDE_HOME");
		}

		if (home == null || home.trim().length() == 0) {
			throw new RuntimeException(
					"请配置环境变量{TEAMIDE_HOME}！(please configure environment variables {TEAMIDE_HOME}!)");
		} else {
			home = home.replaceAll("\\\\", "/");
		}
		if (!home.endsWith("/")) {
			home = home + "/";
		}
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
