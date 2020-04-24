package com.teamide.terminal;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import com.sun.jna.Platform;
import com.teamide.util.StringUtil;

public class TerminalProcess {

	public String charset;

	public TerminalProcess() {

	}

	public void process(String command, File workFolder, TerminalProcessListener listener) throws Exception {
		String[] cmd = null;
		if (Platform.isWindows()) {
			cmd = new String[] { "cmd" };
		} else {
			cmd = new String[] { "/bin/sh" };
		}
		Process process = Runtime.getRuntime().exec(cmd);

		InputStream inputStream = process.getInputStream();
		InputStream errorStream = process.getErrorStream();
		new Thread() {
			@Override
			public void run() {
				read(inputStream, listener);
				listener.onStop();
				process.destroy();
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				read(errorStream, listener);
			}
		}.start();
		if (StringUtil.isNotEmpty(command)) {
			PrintWriter writer = new PrintWriter(process.getOutputStream());
			if (workFolder != null && workFolder.exists()) {
				writer.println("cd " + workFolder.getAbsolutePath());
				writer.flush();
			}
			writer.println(command);
			writer.flush();
			writer.close();
		}
		listener.onStart(getPID(process));
	}

	public void read(InputStream stream, TerminalProcessListener listener) {
		BufferedReader read = null;
		try {
			if (StringUtil.isEmpty(charset)) {
				charset = "UTF-8";
			}
			InputStreamReader inputStreamReader = new InputStreamReader(stream, charset);
			read = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = read.readLine()) != null) {
				if (listener != null) {
					listener.onLog(line);
				}
			}
		} catch (Exception e) {
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public long getPID(Process process) {

		long pid = -1;
		if (process != null) {

			Field field = null;
			if (Platform.isWindows()) {
				try {
					field = process.getClass().getDeclaredField("handle");
					field.setAccessible(true);
					pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(process));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (Platform.isLinux() || Platform.isAIX()) {
				try {
					Class<?> clazz = Class.forName("java.lang.UNIXProcess");
					field = clazz.getDeclaredField("pid");
					field.setAccessible(true);
					pid = (Integer) field.get(process);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else {
			}
		}
		return pid;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
