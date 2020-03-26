package com.teamide.terminal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TerminalUtil {

	public static List<String> getRunningInfoForLinux(String info) throws Exception {
		String shell = "ps -ef|grep '" + info + "' | grep -v grep";
		String[] cmd = new String[] { "/bin/sh" };
		Process process = Runtime.getRuntime().exec(cmd);

		InputStream inputStream = process.getInputStream();
		InputStream errorStream = process.getErrorStream();

		PrintWriter writer = new PrintWriter(process.getOutputStream());
		writer.println(shell);
		writer.flush();
		writer.close();

		List<String> infos = new ArrayList<String>();
		BufferedReader read = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			read = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = read.readLine()) != null) {
				if (line.indexOf(info) >= 0) {
					infos.add(line);
				}
			}
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (Exception e) {
				}
			}
		}
		read = null;
		StringBuffer error = new StringBuffer();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(errorStream, "UTF-8");
			read = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = read.readLine()) != null) {
				error.append(line).append("\n");
			}
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (Exception e) {
				}
			}
		}
		if (error.length() > 0) {
			throw new Exception(error.toString());
		}
		return infos;
	}

	public static List<String> getRunningPidsForLinux(String info) throws Exception {
		String shell = "ps -ef|grep '" + info + "' | grep -v grep | awk '{print $2}'";
		String[] cmd = new String[] { "/bin/sh" };
		Process process = Runtime.getRuntime().exec(cmd);

		InputStream inputStream = process.getInputStream();
		InputStream errorStream = process.getErrorStream();

		PrintWriter writer = new PrintWriter(process.getOutputStream());
		writer.println(shell);
		writer.flush();
		writer.close();

		List<String> infos = new ArrayList<String>();
		BufferedReader read = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			read = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = read.readLine()) != null) {
				infos.add(line);
			}
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (Exception e) {
				}
			}
		}
		read = null;
		StringBuffer error = new StringBuffer();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(errorStream, "UTF-8");
			read = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = read.readLine()) != null) {
				error.append(line).append("\n");
			}
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (Exception e) {
				}
			}
		}
		if (error.length() > 0) {
			throw new Exception(error.toString());
		}
		return infos;
	}
}
