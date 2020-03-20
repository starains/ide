package com.teamide.ide.processor.repository.deployer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.http.HttpRequest;
import com.teamide.http.HttpResponse;
import com.teamide.http.Method;
import com.teamide.ide.bean.DeployServerBean;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.enums.DeployStatus;
import com.teamide.ide.enums.OptionType;
import com.teamide.ide.enums.RunStatus;
import com.teamide.ide.enums.UploadStatus;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.RepositoryLog;
import com.teamide.ide.service.impl.DeployServerService;
import com.teamide.util.FileUtil;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.StringUtil;

public class Deployer {

	protected final RepositoryProcessorParam param;

	protected RunStatus status = RunStatus.STOPPED;

	protected DeployStatus deploystatus = DeployStatus.NOT_DEPLOYED;

	protected UploadStatus uploadstatus = UploadStatus.NOT_UPLOAD;

	protected final JSONObject deployer_info = new JSONObject();

	public final String token;

	protected final File deployerRootFolder;

	protected final File projectFolder;

	protected final File deployerJSONFile;

	protected final File deployerCompileShellFile;

	protected final JSONObject option;

	protected final DeployServerBean server;

	public Deployer(RepositoryProcessorParam param, String path, String name) {
		this.token = IDGenerateUtil.generateShort();
		this.param = param;

		this.deployerRootFolder = param.getTokenDeployerFolder(this.token);
		this.deployerJSONFile = new File(this.deployerRootFolder, "deployer.json");
		if (IDEConstant.IS_OS_WINDOW) {
			this.deployerCompileShellFile = new File(this.deployerRootFolder, "compile.bat");
		} else {
			this.deployerCompileShellFile = new File(this.deployerRootFolder, "compile.sh");
		}
		this.projectFolder = new File(param.getSourceFolder(), path);
		JSONObject option = new JSONObject();
		try {
			option = param.getOption(path, name, OptionType.DEPLOYER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.option = option;
		String serverid = this.option.getString("serverid");
		DeployServerBean server = null;
		if (!StringUtil.isEmpty(serverid)) {
			try {
				server = new DeployServerService().get(serverid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.server = server;

		deployer_info.put("token", this.token);
		deployer_info.put("path", path);
		deployer_info.put("serverid", serverid);
		deployer_info.put("name", name);
		deployer_info.put("status", status);
		deployer_info.put("deploystatus", deploystatus);
		deployer_info.put("uploadstatus", uploadstatus);
		deployer_info.put("option", option);
		deployer_info.put("server", server);
		deployer_info.put("class", this.getClass().getName());

		save();
	}

	public Deployer(RepositoryProcessorParam param, JSONObject json) {
		String token = json.getString("token");
		String path = json.getString("path");
		this.param = param;
		for (String key : json.keySet()) {
			deployer_info.put(key, json.get(key));
		}
		this.token = token;

		this.deployerRootFolder = param.getTokenDeployerFolder(this.token);
		this.deployerJSONFile = new File(this.deployerRootFolder, "deployer.json");
		if (IDEConstant.IS_OS_WINDOW) {
			this.deployerCompileShellFile = new File(this.deployerRootFolder, "compile.bat");
		} else {
			this.deployerCompileShellFile = new File(this.deployerRootFolder, "compile.sh");
		}
		this.projectFolder = new File(param.getSourceFolder(), path);

		this.option = json.getJSONObject("option");
		DeployServerBean server = null;
		if (json.get("server") != null) {
			try {
				server = json.getJSONObject("server").toJavaObject(DeployServerBean.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.server = server;
		try {
			this.status = RunStatus.valueOf(json.getString("status"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.deploystatus = DeployStatus.valueOf(json.getString("deploystatus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.uploadstatus = UploadStatus.valueOf(json.getString("uploadstatus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() throws Exception {
		try {
			HttpRequest request = new HttpRequest(getRemoveUrl());
			request.method(Method.POST);
			request.body(deployer_info);
			HttpResponse response = request.execute();
			param.getDeployerLog(token).info(response.body());
		} catch (Exception e) {
			param.getDeployerLog(token).error(e.getMessage());
		}

		try {
			FileUtils.deleteDirectory(deployerRootFolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		RepositoryLog log = param.getDeployerLog(token);

		log.remove();
	}

	private Process process;

	public void deploy() throws Exception {
		if (deploystatus == DeployStatus.DEPLOYING) {
			throw new RuntimeException("应用部署中，请稍后.");
		}
		changeStatus(DeployStatus.DEPLOYING);
		new Thread() {
			@Override
			public void run() {
				try {
					compile(true);
				} catch (Exception e) {
					param.getDeployerLog(token).error(e.getMessage());
					changeStatus(DeployStatus.NOT_DEPLOYED);
				}
			}

		}.start();

	}

	public void upload() throws Exception {
		if (server == null) {
			throw new Exception("server is null.");
		}
		if (StringUtil.isEmpty(server.getServer())) {
			throw new Exception("server url is null.");
		}
		String sourcepath = option.getString("sourcepath");
		String targetpath = option.getString("targetpath");
		if (StringUtil.isEmpty(sourcepath)) {
			return;
		}
		File file = new File(projectFolder, sourcepath);
		if (!file.exists()) {
			throw new Exception(sourcepath + " not exist.");
		}

		changeStatus(UploadStatus.UPLOADING);
		try {
			param.getDeployerLog(token).info("update start");
			param.getDeployerLog(token).info("update sourcepath : " + sourcepath);
			param.getDeployerLog(token).info("update targetpath : " + targetpath);

			uploadFile(file, file);
			changeStatus(UploadStatus.UPLOADED);
		} catch (Exception e) {
			changeStatus(UploadStatus.NOT_UPLOAD);
		}
	}

	public void uploadFile(File root, File file) {
		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for (File f : fs) {
				uploadFile(root, f);
			}
			return;
		}
		String rootPath = root.toURI().getPath();
		String filePath = file.toURI().getPath();
		String name = file.getName();
		if (filePath.length() > rootPath.length()) {
			name = filePath.substring(rootPath.length());
		}

		try {
			HttpRequest request = new HttpRequest(getUploadUrl());
			request.method(Method.POST);
			request.form("option", option.toJSONString());
			request.form("token", token);
			request.form(name, file);
			param.getDeployerLog(token).info("update file : " + name);
			HttpResponse response = request.execute();
			param.getDeployerLog(token).info(response.body());
		} catch (Exception e) {
			param.getDeployerLog(token).error(e.getMessage());
		}

	}

	public String getUploadUrl() {
		return server.getServer() + "/deployer/upload";
	}

	public String getStartUrl() {
		return server.getServer() + "/deployer/start";
	}

	public String getStopUrl() {
		return server.getServer() + "/deployer/stop";
	}

	public String getStatusUrl() {
		return server.getServer() + "/deployer/status";
	}

	public String getRemoveUrl() {
		return server.getServer() + "/deployer/remove";
	}

	public void startup() throws Exception {

		changeStatus(RunStatus.STARTING);
		try {
			HttpRequest request = new HttpRequest(getStartUrl());
			request.method(Method.POST);
			request.body(deployer_info);
			HttpResponse response = request.execute();
			param.getDeployerLog(token).info(response.body());
		} catch (Exception e) {
			param.getDeployerLog(token).error(e.getMessage());
		}
		changeStatus(RunStatus.STARTED);
	}

	public void shutdown() throws Exception {

		changeStatus(RunStatus.STOPPING);
		try {
			HttpRequest request = new HttpRequest(getStopUrl());
			request.method(Method.POST);
			request.body(deployer_info);
			HttpResponse response = request.execute();
			param.getDeployerLog(token).info(response.body());
		} catch (Exception e) {
			param.getDeployerLog(token).error(e.getMessage());
		}
		changeStatus(RunStatus.STOPPED);
	}

	public void compile(boolean runStart) throws Exception {
		param.getDeployerLog(token).info("compile start");
		String compilescript = option.getString("compilescript");
		if (!StringUtil.isEmpty(compilescript)) {

			createShell(this.deployerCompileShellFile, compilescript);
			if (IDEConstant.IS_OS_WINDOW) {
				String[] cmds = new String[] { "cmd.exe", "/c", deployerCompileShellFile.getAbsolutePath() };
				process = Runtime.getRuntime().exec(cmds, null, this.projectFolder);
			} else {
				String[] cmds = new String[] { deployerCompileShellFile.toURI().getPath() };
				process = Runtime.getRuntime().exec(cmds, null, this.projectFolder);
			}
			new Thread() {

				@Override
				public void run() {
					read(param.getDeployerLog(token), process.getInputStream(), false);
					param.getDeployerLog(token).info("compile end");
					changeStatus(DeployStatus.DEPLOYED);
					if (runStart) {
						try {
							upload();
							startup();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}.start();
			new Thread() {

				@Override
				public void run() {
					read(param.getDeployerLog(token), process.getErrorStream(), true);
				}

			}.start();
		}

	}

	public void read(RepositoryLog log, InputStream stream, boolean isError) {
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
					log.error(line);
				} else {
					log.info(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createShell(File file, String shell) throws Exception {
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

	public JSONObject getDeployerInfo() throws Exception {
		try {
			HttpRequest request = new HttpRequest(getStatusUrl());
			request.method(Method.POST);
			request.body(deployer_info);
			HttpResponse response = request.execute();
			String body = response.body();
			param.getDeployerLog(token).info(body);
			JSONObject result = JSONObject.parseObject(body);
			if (result.get("status") != null) {
				status = RunStatus.valueOf(result.getString("status"));
			}
		} catch (Exception e) {
			param.getDeployerLog(token).error(e.getMessage());
		}

		deployer_info.put("status", status);
		return deployer_info;
	}

	public void changeStatus(RunStatus status) {

		if (this.status != status) {
			this.status = status;
			deployer_info.put("status", status);
			param.getDeployerLog(token).info("deployer status change:" + status);
			save();
		}
	}

	public void changeStatus(DeployStatus status) {

		if (this.deploystatus != status) {
			this.deploystatus = status;
			deployer_info.put("deploystatus", status);
			param.getDeployerLog(token).info("deployer deploy status change:" + status);
			save();
		}
	}

	public void changeStatus(UploadStatus status) {

		if (this.uploadstatus != status) {
			this.uploadstatus = status;
			deployer_info.put("uploadstatus", status);
			param.getDeployerLog(token).info("deployer upload status change:" + status);
			save();
		}
	}

	public void save() {
		try {
			FileUtil.write(deployer_info.toJSONString().getBytes(), deployerJSONFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
