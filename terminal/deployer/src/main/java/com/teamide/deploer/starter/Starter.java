package com.teamide.deploer.starter;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.enums.StarterStatus;
import com.teamide.deploer.enums.TerminalEvent;
import com.teamide.ide.constant.IDEConstant;

public class Starter extends StarterParam {

	public Starter(File starterFolder) {
		super(starterFolder);
	}

	public void destroy() {
		writeEvent(TerminalEvent.DESTROY.getValue());
	}

	public void start() {
		if (starterJarFile != null && starterJarFile.exists()) {
			writeEvent(TerminalEvent.START.getValue());
		} else {
			getLog().error("starter jar does not exist.");
			writeStatus(StarterStatus.DESTROYED);
		}
	}

	public void stop() {
		if (starterJarFile != null && starterJarFile.exists()) {
			writeEvent(TerminalEvent.STOP.getValue());
		} else {
			getLog().error("starter jar does not exist.");
			writeStatus(StarterStatus.DESTROYED);
		}
	}

	public void remove() {
		getLog().remove();
		try {
			FileUtils.deleteDirectory(starterFolder);
		} catch (Exception e) {
		}
	}

	public void cleanLog() {
		log.clean();
	}

	Process process;

	public void startStarter() throws Exception {
		// Process process;
		getLog().info("starting starter...");

		StringBuffer shell = new StringBuffer();

		shell.append(" java");

		shell.append(" -Xms14m -Xmx14m");

		shell.append(" -Dfile.encoding=UTF-8 ");
		shell.append(" -DSTARTER_HOME=\"").append(starterFolder.getAbsolutePath() + "\"");

		if (starterJSON.get("WORK_HOME") != null) {
			shell.append(" -DWORK_HOME=\"")
					.append(new File(starterFolder, starterJSON.getString("WORK_HOME")).getAbsolutePath() + "\"");
		}
		if (starterJSON.get("PID_FILE") != null) {
			shell.append(" -DPID_FILE=\"")
					.append(new File(starterFolder, starterJSON.getString("PID_FILE")).getAbsolutePath() + "\"");
			shell.append(" -DBACKSTAGE=1");
		}

		shell.append(" -jar ");
		shell.append(starterJarFile.getAbsolutePath());
		shell.append(" ");
		getLog().info("starter start shell:" + shell);

		if (IDEConstant.IS_OS_WINDOW) {
			process = Runtime.getRuntime().exec("cmd.exe /c " + shell.toString() + " ");
		} else {
			process = Runtime.getRuntime().exec("" + shell.toString() + " ");
		}

		if (process != null) {
			new Thread() {
				@Override
				public void run() {
					read(process.getInputStream(), false);
				}
			}.start();
			new Thread() {
				@Override
				public void run() {
					read(process.getErrorStream(), true);
				}
			}.start();
		}

		getLog().info("started starter...");
	}

	public JSONObject getStarterInfo() {
		JSONObject json = starterJSON;
		if (json != null) {
			json = (JSONObject) json.clone();
			json.put("status", readStatus());
			json.put("deploy_status", readDeployStatus());
			json.put("install_status", readInstallStatus());
			json.put("starter_running", starterRunning());
		}

		return json;
	}
}
