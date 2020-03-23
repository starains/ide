package com.teamide.deploer.starter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.tool.LogTool;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class StarterParam {

	public final String token;

	public final File starterFolder;

	public final File starterServerFolder;

	public final File starterStartShellFile;

	public final File starterStopShellFile;

	public final File starterJSONFile;

	public final File starterEventFile;

	public final File starterStatusFile;

	public final File starterDeployStatusFile;

	public final File starterJarFile;

	public final File starterTimestampFile;

	public final JSONObject starterJSON;

	public final LogTool log;

	public StarterParam(File starterFolder) {
		this.starterFolder = starterFolder;
		this.starterServerFolder = new File(this.starterFolder, "server");
		this.starterJSONFile = new File(this.starterFolder, "starter.json");

		this.starterEventFile = new File(this.starterFolder, "starter.event");
		this.starterStatusFile = new File(this.starterFolder, "starter.status");
		this.starterDeployStatusFile = new File(this.starterFolder, "starter.deploy.status");
		this.starterTimestampFile = new File(this.starterFolder, "starter.timestamp");
		this.starterJarFile = new File(this.starterFolder, "starter.jar");

		this.starterStartShellFile = new File(this.starterFolder, "starter.start.shell");
		this.starterStopShellFile = new File(this.starterFolder, "starter.stop.shell");
		JSONObject starterJSON = readJSON();
		if (starterJSON == null || starterJSON.isEmpty()) {
			throw new RuntimeException(starterFolder + " starter json is null.");
		}
		this.starterJSON = starterJSON;

		this.token = starterJSON.getString("token");
		log = LogTool.get("starter", new File(starterFolder, "log"));

	}

	public void read(InputStream stream, boolean isError) {
		try {
			String charset = "UTF-8";
			// if (IDEConstant.IS_OS_WINDOW) {
			// charset = "GBK";
			// }
			InputStreamReader inputStreamReader = new InputStreamReader(stream, charset);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (isError) {
					log.error(line);
				} else {
					log.info(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String formatToRoot(String path) {
		if (path == null) {
			return null;
		}
		path = path.replaceAll("\\\\", "/");
		if (path.endsWith("/bin")) {
			path = path.substring(0, path.length() - 4);
		}
		if (path.endsWith("/bin/")) {
			path = path.substring(0, path.length() - 5);
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		return path;
	}

	public String readStatus() {

		try {
			if (starterStatusFile.exists()) {
				return new String(FileUtil.read(starterStatusFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String readDeployStatus() {

		try {
			if (starterDeployStatusFile.exists()) {
				return new String(FileUtil.read(starterDeployStatusFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String writeStatus(String status) {

		try {
			FileUtil.write(status.getBytes(), starterStatusFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String writeDeployStatus(String status) {

		try {
			FileUtil.write(status.getBytes(), starterDeployStatusFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public void writeEvent(String event) {

		try {
			FileUtil.write(event.getBytes(), starterEventFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public long readTimestamp() {

		try {
			if (starterTimestampFile.exists()) {
				String value = new String(FileUtil.read(starterTimestampFile));
				if (StringUtil.isNotEmpty(value)) {
					return Long.valueOf(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}

	public File getStarterLogFolder() {
		return new File(starterFolder, "log");
	}

	public LogTool getLog() {
		return log;
	}

	public JSONObject readJSON() {
		if (starterJSONFile.exists()) {
			try {
				String content = new String(FileUtil.read(starterJSONFile));
				return JSONObject.parseObject(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
