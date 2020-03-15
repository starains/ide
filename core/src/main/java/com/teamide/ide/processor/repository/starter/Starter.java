package com.teamide.ide.processor.repository.starter;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.FileUtil;
import com.teamide.client.ClientSession;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.enums.TerminalEvent;

public class Starter extends StarterParam {

	public Starter(ClientSession session, String token) {
		super(session, token);
	}

	public void onChange(File folder) throws Exception {
		StarterHandler.getStarterLog(token).info("on change:" + folder.toURI().getPath());
		doCompile();
	}

	protected void installServer() throws Exception {

		writeStatus("INSTALL_SERVER");
		StarterHandler.getStarterLog(token).info("starter install server");
		if (starterServerFolder.exists()) {
			FileUtils.deleteDirectory(starterServerFolder);
		}
		File server = starterProcess.getServer();
		if (server != null && server.exists()) {
			starterServerFolder.mkdirs();
			FileUtils.copyDirectory(server, starterServerFolder);
		}
	}

	protected void compile() throws Exception {
		writeStatus("COMPILE");
		doCompile();
	}

	protected void doCompile() throws Exception {
		starterProcess.compile();
	}

	protected void installShell() throws Exception {
		writeStatus("INSTALL_SHELL");
		installStartShell();
	}

	protected void installStartShell() throws Exception {
		writeStatus("INSTALL_SHELL");
		if (starterStartShellFile.exists()) {
			starterStartShellFile.delete();
		}

		starterStartShellFile.createNewFile();
		starterStartShellFile.setExecutable(true);
		String shell = starterProcess.getStartShell();
		if (shell == null) {
			shell = "";
		}
		StarterHandler.getStarterLog(token).info("install start shell");
		StarterHandler.getStarterLog(token).info(shell.toString());
		FileUtil.write(shell.toString().getBytes(), starterStartShellFile);
	}

	protected void installStopShell() throws Exception {
		writeStatus("INSTALL_SHELL");
		if (starterStopShellFile.exists()) {
			starterStopShellFile.delete();
		}

		starterStopShellFile.createNewFile();
		starterStopShellFile.setExecutable(true);
		String shell = starterProcess.getStopShell();
		if (shell == null) {
			shell = "";
		}
		StarterHandler.getStarterLog(token).info("install stop shell");
		StarterHandler.getStarterLog(token).info(shell.toString());
		FileUtil.write(shell.toString().getBytes(), starterStopShellFile);
	}

	public void destroy() {
		try {
			installStarer();
		} catch (Exception e) {
			e.printStackTrace();
			StarterHandler.getStarterLog(token).error(e.getMessage());
			writeStatus("INSTALL_STARER_ERROR");
		}
		writeEvent(TerminalEvent.DESTROY.getValue());
	}

	public void start() {
		try {
			installStarer();
		} catch (Exception e) {
			e.printStackTrace();
			StarterHandler.getStarterLog(token).error(e.getMessage());
			writeStatus("INSTALL_STARER_ERROR");
		}
		try {
			installServer();
		} catch (Exception e) {
			e.printStackTrace();
			StarterHandler.getStarterLog(token).error(e.getMessage());
			writeStatus("INSTALL_SERVER_ERROR");
		}
		try {
			compile();
		} catch (Exception e) {
			e.printStackTrace();
			StarterHandler.getStarterLog(token).error(e.getMessage());
			writeStatus("COMPILE_ERROR");
		}
		try {
			installShell();
		} catch (Exception e) {
			e.printStackTrace();
			StarterHandler.getStarterLog(token).error(e.getMessage());
			writeStatus("INSTALL_SHELL_ERROR");
		}
		if (starterJarFile != null && starterJarFile.exists()) {
			writeEvent(TerminalEvent.START.getValue());
		} else {
			StarterHandler.getStarterLog(token).error("starter jar does not exist.");
			writeStatus("DESTROYED");
		}

	}

	public void stop() {
		try {
			installStarer();
		} catch (Exception e) {
			e.printStackTrace();
			StarterHandler.getStarterLog(token).error(e.getMessage());
			writeStatus("INSTALL_STARER_ERROR");
		}
		writeEvent(TerminalEvent.STOP.getValue());
	}

	public void remove() {
		StarterHandler.getStarterLog(token).remove();
		try {
			FileUtils.deleteDirectory(starterFolder);
		} catch (Exception e) {
		}
	}

	public void installStarer() throws Exception {
		boolean installed = true;
		if (!starterFolder.exists()) {
			installed = false;
		}
		if (readTimestamp() < System.currentTimeMillis() - (1000 * 5)) {
			installed = false;
		}
		if (!installed) {
			StarterHandler.getStarterLog(token).info("install starter start...");
			writeStatus("INSTALL_STARER");
			if (!starterFolder.exists()) {
				starterFolder.mkdirs();
			}
			if (starterJarFile.exists()) {
				starterJarFile.delete();
			}
			File jar = new File(IDEConstant.PLUGIN_STARTER_JAR);
			if (jar != null && jar.exists()) {
				FileUtils.copyFile(jar, starterJarFile);
				writeStatus("STARTING_STARTER");
				startStarter();
				writeStatus("STARTED_STARTER");
			} else {
				StarterHandler.getStarterLog(token).error("plugin starter.jar does not exist.");
			}

			StarterHandler.getStarterLog(token).info("install starter end...");
		}
	}

	Process process;

	protected void startStarter() throws Exception {
		// Process process;
		StarterHandler.getStarterLog(token).info("starting starter...");

		StringBuffer shell = new StringBuffer();

		shell.append(" java");

		shell.append(" -Xms14m -Xmx28m");

		shell.append(" -Dfile.encoding=UTF-8 ");
		shell.append(" -DSTARTER_HOME=\"").append(starterFolder.getAbsolutePath() + "\"");
		if (starterProcess.getWorkFolder() != null) {
			shell.append(" -DWORK_HOME=\"").append(starterProcess.getWorkFolder().getAbsolutePath() + "\"");
		}
		if (starterProcess.getPIDFile() != null) {
			shell.append(" -DPID_FILE=\"").append(starterProcess.getPIDFile().getAbsolutePath() + "\"");
			shell.append(" -DBACKSTAGE=1");

		}

		shell.append(" -jar ");
		shell.append(starterJarFile.getAbsolutePath());
		shell.append(" ");
		StarterHandler.getStarterLog(token).info("starter start shell:" + shell);

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

		StarterHandler.getStarterLog(token).info("started starter...");
	}

	public JSONObject getStarterInfo() {

		if (starterJSON != null) {
			starterJSON.put("status", readStatus());
			starterJSON.put("starter_timestamp", readTimestamp());
			starterJSON.put("now_timestamp", System.currentTimeMillis());
		}

		return starterJSON;
	}
}
