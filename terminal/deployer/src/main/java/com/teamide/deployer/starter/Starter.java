package com.teamide.deployer.starter;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deployer.enums.InstallStatus;
import com.teamide.deployer.enums.StarterStatus;
import com.teamide.deployer.enums.TerminalEvent;
import com.teamide.deployer.starter.shell.DefaultInstall;
import com.teamide.deployer.starter.shell.java.JavaInternalStarterProcess;
import com.teamide.deployer.starter.shell.java.JavaJarStarterProcess;
import com.teamide.deployer.starter.shell.java.JavaMainStarterProcess;
import com.teamide.deployer.starter.shell.java.JavaTomcatStarterProcess;
import com.teamide.deployer.starter.shell.node.NodeStarterProcess;
import com.teamide.util.FileUtil;

public class Starter extends StarterParam {

	protected final StarterShell starterShell;

	public Starter(File starterFolder) {
		super(starterFolder);
		this.starterShell = createStarterShell();
	}

	public String getOptionString(String key) {
		if (this.starterJSON == null && this.starterJSON.get("option") == null) {
			return null;
		}
		JSONObject option = this.starterJSON.getJSONObject("option");
		return option.getString(key);
	}

	public StarterShell createStarterShell() {
		if (this.starterJSON == null && this.starterJSON.get("option") == null) {
			return null;
		}
		JSONObject option = this.starterJSON.getJSONObject("option");
		StarterShell shell = null;
		String language = option.getString("language");
		switch (String.valueOf(language)) {
		case "JAVA":
			String mode = option.getString("mode");
			switch (String.valueOf(mode)) {
			case "MAIN":
				shell = new JavaMainStarterProcess(this);
				break;
			case "JAR":
				shell = new JavaJarStarterProcess(this);
				break;
			case "TOMCAT":
				if (option.getBooleanValue("useinternal")) {
					shell = new JavaInternalStarterProcess(this);
				} else {
					shell = new JavaTomcatStarterProcess(this);
				}
				break;
			}
			break;
		case "NODE":
			shell = new NodeStarterProcess(this);
			break;
		}
		if (shell == null) {
			shell = new DefaultInstall(this);
		}
		return shell;

	}

	public void start() throws Exception {
		installShell();
		writeEvent(TerminalEvent.START.getValue());
	}

	public void stop() throws Exception {
		writeEvent(TerminalEvent.STOP.getValue());
	}

	public void destroy() throws Exception {
		writeEvent(TerminalEvent.DESTROY.getValue());
	}

	public void remove() throws Exception {
		writeStatus(StarterStatus.DESTROYING);
		destroy();
		try {
			Thread.sleep(1000 * 3);
			getLog().remove();
			FileUtils.deleteDirectory(starterFolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void installShell() throws Exception {
		writeInstallStatus(InstallStatus.INSTALL_SHELL);
		installStartShell();
		installStopShell();

		File workFolder = this.workFolder;
		File pidFile = this.starterShell.getPIDFile();

		if (workFolder != null) {
			String path = workFolder.getAbsolutePath().substring(this.starterFolder.getAbsolutePath().length());
			this.starterJSON.put("WORK_FOLDER", path);
		} else {
			this.starterJSON.remove("WORK_FOLDER");
		}

		if (pidFile != null) {
			String path = pidFile.getAbsolutePath().substring(this.starterFolder.getAbsolutePath().length());
			this.starterJSON.put("PID_FILE", path);
			this.starterJSON.put("BACKSTAGE", "1");

		} else {
			this.starterJSON.remove("PID_FILE");
			this.starterJSON.remove("BACKSTAGE");
		}

		this.starterJSON.put("READYED", true);
		FileUtil.write(starterJSON.toJSONString().getBytes(), starterJSONFile);
	}

	protected void installStartShell() throws Exception {
		writeInstallStatus(InstallStatus.INSTALL_SHELL);
		if (starterStartShellFile.exists()) {
			starterStartShellFile.delete();
		}

		starterStartShellFile.createNewFile();
		starterStartShellFile.setExecutable(true);
		String shell = this.starterShell.getStartShell();
		if (shell == null) {
			shell = "";
		}
		getLog().info("install start shell");
		getLog().info(shell.toString());
		FileUtil.write(shell.toString().getBytes(), starterStartShellFile);
	}

	protected void installStopShell() throws Exception {
		writeInstallStatus(InstallStatus.INSTALL_SHELL);
		if (starterStopShellFile.exists()) {
			starterStopShellFile.delete();
		}

		starterStopShellFile.createNewFile();
		starterStopShellFile.setExecutable(true);
		String shell = this.starterShell.getStopShell();
		if (shell == null) {
			shell = "";
		}
		getLog().info("install stop shell");
		getLog().info(shell.toString());
		FileUtil.write(shell.toString().getBytes(), starterStopShellFile);
	}

	public void cleanLog() {
		log.clean();
	}

	public JSONObject getStarterInfo() {
		JSONObject json = starterJSON;
		if (json != null) {
			json = (JSONObject) json.clone();
			json.put("status", readStatus());
			json.put("deploy_status", readDeployStatus());
			json.put("install_status", readInstallStatus());
		}

		return json;
	}
}
