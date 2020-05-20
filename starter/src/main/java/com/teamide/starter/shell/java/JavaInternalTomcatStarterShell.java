package com.teamide.starter.shell.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.teamide.shell.java.JavaShell;
import com.teamide.starter.StarterParam;
import com.teamide.util.StringUtil;

public class JavaInternalTomcatStarterShell extends JavaStarterShell {

	public JavaInternalTomcatStarterShell(StarterParam param) {
		super(param);

	}

	public File appFolder;

	@Override
	public String getStartShell() throws Exception {

		String hostname = option.getHostname();
		Integer port = option.getPort();
		String app = appFolder.getAbsolutePath();
		String contextpath = option.getContextpath();

		JavaShell shell = (JavaShell) this.shell;
		shell.setJava_home(getJavaHome());
		shell.setJava_envp(getJavaEnvp());

		List<File> lib_folders = new ArrayList<File>();
		lib_folders.add(new File(param.starterServerFolder, "lib"));
		shell.setLib_folders(lib_folders);

		Map<String, String> envps = new HashMap<String, String>();
		envps.put("STARTER_TOKEN", param.token);
		envps.put("SERVER_ROOT", param.starterServerFolder.getAbsolutePath());
		envps.put("SERVER_PORT", String.valueOf(port));
		envps.put("SERVER_HOSTNAME", hostname);
		envps.put("SERVER_CONTEXTPATH", contextpath);
		envps.put("SERVER_APP", app);
		shell.setEnvps(envps);

		shell.setMain("com.teamide.server.ServerMain");

		return shell.getShellString();
	}

	@Override
	public String getStopShell() throws Exception {
		return null;
	}

	@Override
	public File getPIDFile() throws Exception {
		return shell.getPIDFile();
	}

	@Override
	public void startReady() throws Exception {
		outAppFolder(param.starterServerFolder);
	}

	public void initAppFolder(File tomcatFolder, String name) {
		File webappsFolder = new File(tomcatFolder, "webapps");
		String contextpath = getContextpath();
		if (StringUtil.isEmpty(contextpath)) {
			contextpath = name;
		}
		String outName = contextpath;
		if (StringUtil.isEmpty(outName) || outName.equals("/")) {
			outName = "/ROOT";
		}
		appFolder = new File(webappsFolder, outName);

	}

	protected void outAppFolder(File tomcatFolder) throws Exception {
		File folder = new File(this.param.starterFolder, "work");
		File targetWebappFolder = new File(folder, "webapp");
		if (targetWebappFolder.exists() && targetWebappFolder.listFiles().length > 0) {
			targetWebappFolder = targetWebappFolder.listFiles()[0];
		}
		if (targetWebappFolder != null && targetWebappFolder.exists()) {
			if (tomcatFolder == null || !tomcatFolder.exists()) {
				throw new Exception("tomcat[" + tomcatFolder.getAbsolutePath() + "] does not exist.");
			}

			initAppFolder(tomcatFolder, targetWebappFolder.getName());

			if (appFolder.exists()) {
				try {
					FileUtils.deleteDirectory(appFolder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!appFolder.exists()) {
				appFolder.mkdirs();
			}
			try {
				FileUtils.copyDirectory(targetWebappFolder, appFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	protected void outAppWar(File tomcatFolder) throws Exception {
		File folder = new File(this.param.starterFolder, "work");

		File warFile = null;
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".war")) {
				warFile = file;
				break;
			}
		}

		if (warFile != null && warFile.exists()) {
			if (tomcatFolder == null || !tomcatFolder.exists()) {
				throw new Exception("tomcat[" + tomcatFolder.getAbsolutePath() + "] does not exist.");
			}

			initAppFolder(tomcatFolder, warFile.getName());
			if (appFolder.getName().endsWith(".war")) {

			} else {
				appFolder = new File(appFolder.getParentFile(), appFolder.getName() + ".war");
			}

			if (appFolder.exists()) {
				try {
					FileUtils.forceDelete(appFolder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				FileUtils.copyFile(warFile, appFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
