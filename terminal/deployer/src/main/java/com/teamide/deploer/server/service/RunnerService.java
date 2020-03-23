package com.teamide.deploer.server.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.IDEConstant;
import com.teamide.deploer.util.StringUtil;

public class RunnerService {
	public static File getShellFolder(String token) {
		return new File(IDEConstant.WORKSPACES_STARTER_FOLDER, token + "/shell");
	}

	public static File getRoot(String token, JSONObject option) {
		String targetpath = option.getString("targetpath");
		if (StringUtil.isEmpty(targetpath)) {
			if (StringUtil.isEmpty(token)) {
				return new File(IDEConstant.WORKSPACES_STARTER_FOLDER, option.getString("name") + "/source");
			} else {
				return new File(IDEConstant.WORKSPACES_STARTER_FOLDER, token + "/source");
			}
		} else {
			return new File(targetpath);
		}
	}

	public JSONObject start(String token, JSONObject option) throws Exception {
		JSONObject result = new JSONObject();
		String script = option.getString("script");
		File shellFolder = getShellFolder(token);
		if (!StringUtil.isEmpty(script)) {
			File shell = null;
			if (IDEConstant.IS_OS_WINDOW) {
				shell = new File(shellFolder, "start.bat");
			} else {
				shell = new File(shellFolder, "start.sh");
			}
			createShell(shellFolder, script);
			runShell(shell, getRoot(token, option));
		}
		return result;
	}

	public JSONObject stop(String token, JSONObject option) throws Exception {
		JSONObject result = new JSONObject();
		String script = option.getString("script");
		File shellFolder = getShellFolder(token);
		if (!StringUtil.isEmpty(script)) {
			File shell = null;
			if (IDEConstant.IS_OS_WINDOW) {
				shell = new File(shellFolder, "stop.bat");
			} else {
				shell = new File(shellFolder, "stop.sh");
			}
			createShell(shellFolder, script);
			runShell(shell, getRoot(token, option));
		} else {
			String pid = getPid(token, option);
			if (!StringUtil.isEmpty(pid)) {
				if (IDEConstant.IS_OS_LINUX) {
					Process p = Runtime.getRuntime().exec("kill -9 " + pid);
					InputStream in = p.getInputStream();
					deletePidFile(token, option);
					if (in != null) {
						in.close();
					}
				}
			}
		}
		return result;
	}

	public void deletePidFile(String token, JSONObject option) {

		String pidpath = option.getString("pidpath");
		File root = getRoot(token, option);
		if (!StringUtil.isEmpty(pidpath)) {
			File pidFile = new File(root, pidpath);
			if (pidFile.exists()) {
				pidFile.delete();
			}
		}
	}

	public String getPid(String token, JSONObject option) {
		String pidpath = option.getString("pidpath");
		File root = getRoot(token, option);
		if (!StringUtil.isEmpty(pidpath)) {
			File pidFile = new File(root, pidpath);
			if (pidFile.exists()) {
				try {
					String pid = FileUtils.readFileToString(pidFile);
					if (!StringUtil.isEmpty(pid)) {
						pid = pid.replace("\n", "");
						pid = pid.trim();
						return pid;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public JSONObject status(String token, JSONObject option) throws Exception {
		JSONObject result = new JSONObject();
		String pid = getPid(token, option);
		String status = "STOPPED";
		if (!StringUtil.isEmpty(pid)) {
			if (IDEConstant.IS_OS_LINUX) {
				Process p = Runtime.getRuntime().exec("ps -q " + pid);
				InputStream in = p.getInputStream();
				try {
					byte[] bytes = new byte[in.available()];
					IOUtils.read(in, bytes);
					if (bytes != null) {
						String info = new String(bytes);
						if (info.indexOf(pid + "") > 0) {
							status = "STARTED";
						} else {
							status = "STOPPED";
							deletePidFile(token, option);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}
		}
		result.put("status", status);
		return result;
	}

	public JSONObject remove(String token, JSONObject option) throws Exception {
		JSONObject result = new JSONObject();
		return result;
	}

	private void runShell(File shell, File root) throws Exception {
		if (!shell.exists()) {
			return;
		}
		Process process;
		if (IDEConstant.IS_OS_WINDOW) {
			String[] cmds = new String[] { "cmd.exe", "/c", shell.getAbsolutePath() };
			process = Runtime.getRuntime().exec(cmds, null, root);
		} else {
			String[] cmds = new String[] { shell.toURI().getPath() };
			process = Runtime.getRuntime().exec(cmds, null, root);
		}
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

	public void read(InputStream stream, boolean isError) {
		try {
			InputStreamReader inputStreamReader;
			if (IDEConstant.IS_OS_WINDOW) {
				inputStreamReader = new InputStreamReader(stream, "GBK");
			} else {
				inputStreamReader = new InputStreamReader(stream);
			}
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (isError) {
					System.err.println(line);
					// log.error(line);
				} else {
					System.out.println(line);
					// log.info(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createShell(File file, String shell) throws Exception {
		if (file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		file.setExecutable(true);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bf = new BufferedWriter(fw);
		bf.write(shell);
		bf.flush();
		bf.close();
	}
}
