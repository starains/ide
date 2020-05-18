package com.teamide.starter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.sun.jna.Platform;
import com.teamide.starter.bean.JavaOptionBean;
import com.teamide.starter.enums.StarterStatus;
import com.teamide.starter.shell.DefaultStarterShell;
import com.teamide.starter.shell.java.JavaInternalTomcatStarterShell;
import com.teamide.starter.shell.java.JavaJarStarterShell;
import com.teamide.starter.shell.java.JavaMainStarterShell;
import com.teamide.starter.shell.java.JavaWarStarterShell;
import com.teamide.terminal.TerminalProcess;
import com.teamide.terminal.TerminalProcessListener;
import com.teamide.terminal.TerminalUtil;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class StarterEventProcessor extends StarterParam {

	protected final StarterShell starterShell;

	public final File starterLockFile;

	public final String user;

	public StarterEventProcessor(File starterFolder, String user) {
		super(starterFolder);
		this.user = user;
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
			starterShell.startReady();
			File workFolder = starterShell.getWorkFolder();
			File pidFile = this.starterShell.getPIDFile();

			String command = getStartShell();
			if (StringUtil.isNotEmpty(command)) {
				TerminalProcess process = new TerminalProcess(user);

				getLog().info("start shell command:" + command);
				process.process(command, workFolder, new TerminalProcessListener() {

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
				TerminalProcess process = new TerminalProcess(user);
				File workFolder = starterShell.getWorkFolder();

				getLog().info("stop shell command:" + command);
				process.process(command, workFolder, new TerminalProcessListener() {
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

	public void kill(String pidStr, TerminalProcessListener listener) throws Exception {
		if (StringUtil.isEmpty(pidStr)) {
			return;
		}
		pidStr = pidStr.replaceAll("\n", " ");
		pidStr = pidStr.trim();
		if (StringUtil.isEmpty(pidStr)) {
			return;
		}
		if (Integer.valueOf(pidStr) < 300) {
			return;
		}
		String pid = pidStr;
		TerminalProcess process = new TerminalProcess(user);
		String command = null;
		if (Platform.isWindows()) {
			command = "taskkill /PID " + pid + " /F /T";
			getLog().info("kill pid " + pid);
			process.process(command, null, listener);
		} else {
			getLog().info("pkill pid " + pid);
			// 根据父进程编号杀死子进程
			process.process("pkill -9 -P " + pid, null, new TerminalProcessListener() {
				@Override
				public void onStop() {
					String command = "kill -9 " + pid;
					getLog().info("kill pid " + pid);
					try {
						process.process(command, null, listener);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
		StarterShell shell = null;
		if (option instanceof JavaOptionBean) {
			JavaOptionBean javaOption = (JavaOptionBean) option;
			String mode = javaOption.getMode();
			switch (String.valueOf(mode)) {
			case "MAIN":
				shell = new JavaMainStarterShell(this);
				break;
			case "JAR":
				shell = new JavaJarStarterShell(this);
				break;
			case "WAR":
				shell = new JavaWarStarterShell(this);
				break;
			case "TOMCAT":
				shell = new JavaInternalTomcatStarterShell(this);
				break;
			}
		}
		if (shell == null) {
			shell = new DefaultStarterShell(this);
		}
		return shell;

	}

}
