package com.teamide.terminal;

import java.io.File;

import com.sun.jna.Platform;
import com.teamide.terminal.util.FileUtil;
import com.teamide.terminal.util.StringUtil;

public class TerminalServer implements Runnable {

	long time = 1000 * 1;

	public final TerminalParam param;

	public TerminalServer(TerminalParam param) {
		this.param = param;
	}

	public Status status = Status.STOPPED;

	@Override
	public void run() {
		if (param.getTimestampFile() != null) {
			TerminalServerTimestamp timestamp = new TerminalServerTimestamp(param.getTimestampFile());
			new Thread(timestamp).start();
		}
		while (true) {
			try {
				String event = readEvent();
				if (!StringUtil.isEmpty(event)) {
					writeLog("read event:" + event);
					switch (event) {
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
				}

			} catch (Exception e) {
				writeLog("error:" + e.getMessage());
				e.printStackTrace();
			}
			try {
				Thread.sleep(time);
			} catch (Exception e) {
			}
		}

	}

	public TerminalProcessListener createListener() {
		return new TerminalProcessListener() {
			@Override
			public void onStop() {
				writePID("");
				changeStatus(Status.STOPPED);
			}

			@Override
			public void onStart(long pid) {
				writePID("" + pid);
				changeStatus(Status.STARTED);
			}

			@Override
			public void onLog(String line) {

				writeLog(line);
			}

		};
	}

	public void start() {
		changeStatus(Status.STARTING);
		kill();
		try {

			String command = readStartShell();
			if (StringUtil.isNotEmpty(command)) {
				TerminalProcess process = new TerminalProcess();

				writeLog("start shell command");
				writeLog(command);
				process.start(command, param.getWorkFolder(), new TerminalProcessListener() {

					@Override
					public void onStop() {
						if (!param.isBackstage()) {
							changeStatus(Status.STOPPED);
							writeStartShellPID("");
						} else {
							try {
								Thread.sleep(1000 * 3);
								String pid = readPID();
								if (StringUtil.isEmpty(pid)) {
									changeStatus(Status.STOPPED);
								}
							} catch (Exception e) {
							}
						}
					}

					@Override
					public void onStart(long pid) {
						writeStartShellPID(String.valueOf(pid));
						changeStatus(Status.STARTED);
					}

					@Override
					public void onLog(String line) {
						writeLog(line);

					}
				});
			}

		} catch (Exception e) {
			writeLog(e.getMessage());
			e.printStackTrace();
			changeStatus(Status.STOPPED);
		}

	}

	public void stop() {
		Status old = status;
		changeStatus(Status.STOPPING);
		try {
			executeStop();
			if (param.isBackstage()) {
				changeStatus(Status.STOPPED);
			}
		} catch (Exception e) {
			writeLog(e.getMessage());
			e.printStackTrace();
			changeStatus(old);
		}
	}

	public void executeStop() throws Exception {
		String command = readStopShell();
		if (StringUtil.isNotEmpty(command)) {
			TerminalProcess process = new TerminalProcess();

			writeLog("stop shell command");
			writeLog(command);
			process.start(command, param.getWorkFolder(), new TerminalProcessListener() {

				@Override
				public void onStop() {
					kill();
				}

				@Override
				public void onStart(long pid) {
				}

				@Override
				public void onLog(String line) {
					writeLog(line);

				}
			});
		} else {
			kill();
		}
	}

	public void destroy() {

		changeStatus(Status.DESTROYING);
		try {
			executeStop();
		} catch (Exception e) {
			writeLog(e.getMessage());
			e.printStackTrace();
		}
		changeStatus(Status.DESTROYED);
		System.exit(1);
	}

	public void kill() {
		killPID();
		killStartShellPID();
	}

	public void killPID() {
		String pid = readPID();
		if (StringUtil.isEmpty(pid)) {
			return;
		}
		try {
			kill(pid, new TerminalProcessListener() {
				@Override
				public void onStop() {
					writePID("");
				}

				@Override
				public void onStart(long pid) {
				}

				@Override
				public void onLog(String line) {
					writeLog(line);
				}
			});
		} catch (Exception e) {
			writeLog("kill PID error:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void killStartShellPID() {
		String pid = readStartShellPID();
		if (StringUtil.isEmpty(pid)) {
			return;
		}
		try {
			kill(pid, new TerminalProcessListener() {
				@Override
				public void onStop() {
					writeStartShellPID("");
				}

				@Override
				public void onStart(long pid) {
				}

				@Override
				public void onLog(String line) {
					writeLog(line);
				}
			});
		} catch (Exception e) {
			writeLog("kill start shell PID error:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void kill(String pid, TerminalProcessListener listener) throws Exception {
		if (StringUtil.isEmpty(pid)) {
			return;
		}
		writeLog("kill pid " + pid);
		TerminalProcess process = new TerminalProcess();
		String command = null;
		if (Platform.isWindows()) {
			command = "taskkill /PID " + pid + " /F /T";
		} else {
			command = "kill -9 " + pid;
		}
		process.start(command, null, listener);
	}

	public void changeStatus(Status status) {
		this.status = status;
		writeLog("change status:" + status.getValue());
		write(status.getValue(), param.getStatusFile());
	}

	public String readEvent() {
		String event = read(param.getEventFile());
		if (!StringUtil.isEmpty(event)) {
			write("", param.getEventFile());
		}
		return event;
	}

	public String readStartShell() {
		return read(param.getStartShellFile());
	}

	public String readStopShell() {
		return read(param.getStopShellFile());
	}

	public void writeStartShellPID(String pid) {
		write(pid, param.getStartShellPidFile());
	}

	public void writePID(String pid) {
		if (param.getPidFile() != null && param.getPidFile().exists()) {
			write(pid, param.getPidFile());
		}
	}

	public String readStartShellPID() {
		String pid = read(param.getStartShellPidFile());
		return pid;
	}

	public String readPID() {
		String pid = read(param.getPidFile());
		return pid;
	}

	public void writeLog(String log) {
		if (param.getLogFile() == null) {
			return;
		}
		try {
			FileUtil.append(log + "\r\n", param.getLogFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(String content, File file) {
		if (file != null) {
			try {
				if (content == null) {
					content = "";
				}
				FileUtil.write(content.getBytes(), file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String read(File file) {
		String content = null;
		if (file != null && file.exists()) {
			try {
				byte[] bytes = FileUtil.read(file);
				if (bytes != null) {
					content = new String(bytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return content;
	}
}
