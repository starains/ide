package com.teamide.starter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Platform;
import com.teamide.starter.enums.StarterStatus;
import com.teamide.starter.shell.DefaultInstall;
import com.teamide.starter.shell.java.JavaInternalStarterProcess;
import com.teamide.starter.shell.java.JavaJarStarterProcess;
import com.teamide.starter.shell.java.JavaMainStarterProcess;
import com.teamide.starter.shell.java.JavaTomcatStarterProcess;
import com.teamide.starter.shell.node.NodeStarterProcess;
import com.teamide.terminal.TerminalProcess;
import com.teamide.terminal.TerminalProcessListener;
import com.teamide.terminal.TerminalUtil;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class StarterEventProcessor extends StarterParam {

	protected final StarterShell starterShell;

	public final File starterLockFile;

	public StarterEventProcessor(File starterFolder) {
		super(starterFolder);
		this.starterLockFile = new File(starterFolder, "starter.lock");
		this.starterShell = createStarterShell();
	}

	public void process() {
		if (starterLockFile.exists()) {
			return;
		}
		try {
			starterLockFile.createNewFile();
			String event = readEvent();
			if (StringUtil.isEmpty(event)) {
				return;
			}
			switch (event.toUpperCase()) {
			case "START":
				start();
				break;
			case "STOP":
				stop();
				break;
			case "DESTROY":
				destroy();
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (starterLockFile.exists()) {
				starterLockFile.delete();
			}
		}
	}

	public void start() {
		writeStatus(StarterStatus.STARTING);
		kill();
		try {
			File workFolder = this.workFolder;
			File pidFile = this.starterShell.getPIDFile();

			String command = getStartShell();
			if (StringUtil.isNotEmpty(command)) {
				TerminalProcess process = new TerminalProcess();

				getLog().info("start shell command:" + command);
				process.start(command, workFolder, new TerminalProcessListener() {

					@Override
					public void onStop() {
						if (pidFile == null) {
							getLog().info("on stop is not backstage, call STOPPED.");
							writeStatus(StarterStatus.STOPPED);
						}
					}

					@Override
					public void onStart(long pid) {
						if (pidFile != null) {
							writeStatus(StarterStatus.STARTED);
							writePID(String.valueOf(pid), pidFile);
						}
					}

					@Override
					public void onLog(String line) {
						getLog().info(line);

					}
				});
			}

		} catch (Exception e) {
			getLog().error(e.getMessage());
			e.printStackTrace();
			writeStatus(StarterStatus.STOPPED);
		}

	}

	public void stop() {
		String old = readStatus();
		writeStatus(StarterStatus.STOPPING);
		try {
			kill();
			File pidFile = this.starterShell.getPIDFile();
			if (pidFile != null) {
				writeStatus(StarterStatus.STOPPED);
			}
		} catch (Exception e) {
			getLog().error(e.getMessage());
			e.printStackTrace();
			if (StringUtil.isNotEmpty(old)) {
				writeStatus(StarterStatus.valueOf(old));
			}
		}

	}

	public void destroy() {
		String old = readStatus();
		writeStatus(StarterStatus.DESTROYING);
		try {
			kill();
			writeStatus(StarterStatus.DESTROYED);
		} catch (Exception e) {
			getLog().error(e.getMessage());
			e.printStackTrace();
			if (StringUtil.isNotEmpty(old)) {
				writeStatus(StarterStatus.valueOf(old));
			}
		}

	}

	public void kill() {
		try {
			String command = getStopShell();
			if (StringUtil.isNotEmpty(command)) {
				TerminalProcess process = new TerminalProcess();
				File workFolder = this.workFolder;

				getLog().info("stop shell command:" + command);
				process.start(command, workFolder, new TerminalProcessListener() {
					@Override
					public void onStop() {
					}

					@Override
					public void onStart(long pid) {
					}

					@Override
					public void onLog(String line) {
						getLog().info(line);

					}
				});
			}

			if (Platform.isLinux()) {
				String info = getSearchInfo();
				if (StringUtil.isNotEmpty(info)) {

					List<String> pids = TerminalUtil.getRunningPidsForLinux(info);
					for (String pid : pids) {

						kill(pid, new TerminalProcessListener() {

							@Override
							public void onStop() {
							}

							@Override
							public void onStart(long pid) {
							}

							@Override
							public void onLog(String line) {
								getLog().info(line);
							}
						});
					}
				}
			}

			File pidFile = this.starterShell.getPIDFile();
			if (pidFile != null) {
				String pid = readPID(pidFile);
				kill(pid, new TerminalProcessListener() {

					@Override
					public void onStop() {
						writePID("", pidFile);
					}

					@Override
					public void onStart(long pid) {

					}

					@Override
					public void onLog(String line) {
						getLog().info(line);
					}
				});
			}

		} catch (Exception e) {
			getLog().error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void kill(String pid, TerminalProcessListener listener) throws Exception {
		if (StringUtil.isEmpty(pid)) {
			return;
		}
		getLog().info("kill pid " + pid);
		TerminalProcess process = new TerminalProcess();
		String command = null;
		if (Platform.isWindows()) {
			command = "taskkill /PID " + pid + " /F /T";
		} else {
			command = "kill -9 " + pid;
		}
		process.start(command, null, listener);
	}

	protected String getStartShell() throws Exception {
		String shell = this.starterShell.getStartShell();
		if (shell == null) {
			shell = "";
		}
		return shell;
	}

	protected String getSearchInfo() throws Exception {
		String shell = this.starterShell.getSearchInfo();
		if (shell == null) {
			shell = "";
		}
		return shell;
	}

	protected String getStopShell() throws Exception {
		String shell = this.starterShell.getStopShell();
		return shell;
	}

	public String readEvent() {
		String content = null;
		if (this.starterEventFile != null && this.starterEventFile.exists()) {
			try {
				byte[] bytes = FileUtil.read(this.starterEventFile);
				if (bytes != null) {
					content = new String(bytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					FileUtil.write("".getBytes(), this.starterEventFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return content;
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

}
