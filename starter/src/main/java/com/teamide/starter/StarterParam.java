package com.teamide.starter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;
import com.teamide.LogTool;
import com.teamide.starter.bean.JavaOptionBean;
import com.teamide.starter.bean.OtherOptionBean;
import com.teamide.starter.bean.StarterOptionBean;
import com.teamide.starter.enums.DeployStatus;
import com.teamide.starter.enums.InstallStatus;
import com.teamide.starter.enums.StarterStatus;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class StarterParam {

	public final String token;

	public final File starterFolder;

	public final File starterServerFolder;

	public final File starterJSONFile;

	public final File starterEventFile;

	public final File starterStatusFile;

	public final File deployStatusFile;

	public final File installStatusFile;

	public final StarterOptionBean option;

	public final LogTool log;

	public StarterParam(File starterFolder) {
		this.starterFolder = starterFolder;
		this.starterServerFolder = new File(this.starterFolder, "server");
		// this.workFolder = new File(this.starterFolder, "work");
		this.starterJSONFile = new File(this.starterFolder, "starter.json");

		this.starterEventFile = new File(this.starterFolder, "starter.event");
		this.starterStatusFile = new File(this.starterFolder, "starter.status");
		this.deployStatusFile = new File(this.starterFolder, "deploy.status");
		this.installStatusFile = new File(this.starterFolder, "install.status");

		JSONObject starterJSON = readJSON();
		if (starterJSON == null || starterJSON.isEmpty()) {
			throw new RuntimeException(starterFolder + " starter json is null.");
		}
		if (StringUtil.isNotEmpty(starterJSON.getString("language"))
				&& starterJSON.getString("language").equalsIgnoreCase("JAVA")) {
			option = starterJSON.toJavaObject(JavaOptionBean.class);
		} else {
			option = starterJSON.toJavaObject(OtherOptionBean.class);
		}

		this.token = option.getToken();
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
			if (deployStatusFile.exists()) {
				return new String(FileUtil.read(deployStatusFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String readInstallStatus() {

		try {
			if (installStatusFile.exists()) {
				return new String(FileUtil.read(installStatusFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String writePID(String pid, File pidFile) {

		try {
			FileUtil.write(pid.getBytes(), pidFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String readPID(File pidFile) {

		try {
			if (pidFile.exists()) {
				return new String(FileUtil.read(pidFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String writeStatus(StarterStatus status) {

		try {
			FileUtil.write(status.getValue().getBytes(), starterStatusFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String writeDeployStatus(DeployStatus status) {

		try {
			FileUtil.write(status.getValue().getBytes(), deployStatusFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String writeInstallStatus(InstallStatus status) {

		try {
			FileUtil.write(status.getValue().getBytes(), installStatusFile);
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
