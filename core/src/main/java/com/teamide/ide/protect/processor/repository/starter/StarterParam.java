package com.teamide.ide.protect.processor.repository.starter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.starter.StarterProcess;
import com.teamide.ide.protect.processor.repository.starter.java.JavaInternalStarterProcess;
import com.teamide.ide.protect.processor.repository.starter.java.JavaJarStarterProcess;
import com.teamide.ide.protect.processor.repository.starter.java.JavaMainStarterProcess;
import com.teamide.ide.protect.processor.repository.starter.java.JavaTomcatStarterProcess;
import com.teamide.ide.protect.processor.repository.starter.node.NodeStarterProcess;
import com.teamide.ide.protect.service.EnvironmentService;

public class StarterParam {

	public final String token;

	public final RepositoryProcessorParam param;

	public final File starterFolder;

	public final File starterServerFolder;

	public final File starterStartShellFile;

	public final File starterStopShellFile;

	public final File starterJSONFile;

	public final File starterEventFile;

	public final File starterStatusFile;

	public final File starterJarFile;

	public final File starterTimestampFile;

	public final File projectFolder;

	public final JSONObject starterJSON;

	public final StarterOption option;

	public final EnvironmentBean environment;

	public final StarterProcess starterProcess;

	public StarterParam(ClientSession session, String token) {
		this.token = token;
		this.starterFolder = StarterHandler.getStarterFolder(token);
		this.starterServerFolder = new File(this.starterFolder, "server");

		this.starterJSONFile = StarterHandler.getStarterJSONFile(token);
		this.starterEventFile = new File(this.starterFolder, "starter.event");
		this.starterStatusFile = new File(this.starterFolder, "starter.status");
		this.starterTimestampFile = new File(this.starterFolder, "starter.timestamp");
		this.starterJarFile = new File(this.starterFolder, "starter.jar");

		this.starterStartShellFile = new File(this.starterFolder, "starter.start.shell");
		this.starterStopShellFile = new File(this.starterFolder, "starter.stop.shell");

		JSONObject starterJSON = StarterHandler.readJSON(token);
		StarterOption option = null;
		File projectFolder = null;
		RepositoryProcessorParam param = null;
		if (starterJSON != null) {
			if (starterJSON.getJSONObject("option") != null) {
				option = starterJSON.getJSONObject("option").toJavaObject(StarterOption.class);
			}
			String spaceid = starterJSON.getString("spaceid");
			String branch = starterJSON.getString("branch");
			String path = starterJSON.getString("path");
			if (!StringUtil.isEmpty(spaceid)) {
				param = new RepositoryProcessorParam(session, spaceid, branch);
				projectFolder = new File(param.getSourceFolder(), path);
			}
		}

		this.param = param;
		this.option = option;
		this.starterJSON = starterJSON;
		this.projectFolder = projectFolder;

		EnvironmentBean environment = null;
		if (this.option != null) {
			String environmentid = this.option.getEnvironmentid();
			if (!StringUtil.isEmpty(environmentid)) {
				try {
					environment = new EnvironmentService().get(environmentid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.environment = environment;

		this.starterProcess = createStarterProcess();
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
					StarterHandler.getStarterLog(token).error(line);
				} else {
					StarterHandler.getStarterLog(token).info(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private StarterProcess createStarterProcess() {
		if (option == null) {
			return null;
		}
		StarterProcess process = null;
		String language = option.getLanguage();
		switch (String.valueOf(language)) {
		case "JAVA":
			String mode = option.getMode();
			switch (String.valueOf(mode)) {
			case "MAIN":
				process = new JavaMainStarterProcess(this);
				break;
			case "JAR":
				process = new JavaJarStarterProcess(this);
				break;
			case "TOMCAT":
				if (option.isUseinternal()) {
					process = new JavaInternalStarterProcess(this);
				} else {
					process = new JavaTomcatStarterProcess(this);
				}
				break;
			}
			break;
		case "NODE":
			process = new NodeStarterProcess(this);
			break;
		}
		if (process == null) {
			process = new DefaultStarterProcess(this);
		}
		return process;
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

	public String writeStatus(String status) {

		try {
			FileUtil.write(status.getBytes(), starterStatusFile);
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
				return Long.valueOf(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}
}
