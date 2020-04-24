package com.teamide.ide.deployer;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.http.HttpRequest;
import com.teamide.http.HttpResponse;
import com.teamide.ide.bean.RemoteBean;
import com.teamide.ide.deployer.install.java.JavaInstall;
import com.teamide.ide.handler.RemoteHandler;
import com.teamide.starter.enums.DeployStatus;
import com.teamide.starter.enums.InstallStatus;
import com.teamide.util.FileUtil;

public class RemoteDeploy extends Deploy {

	private final String remoteid;

	public RemoteDeploy(String remoteid, File starterFolder) {
		super(starterFolder);
		this.remoteid = remoteid;
	}

	public void deploy() throws Exception {
		this.starter.writeDeployStatus(DeployStatus.DEPLOYING);

		install();

		this.starter.writeDeployStatus(DeployStatus.UPLOADING);

		upload();
		this.starter.writeDeployStatus(DeployStatus.UPLOADED);

		start();

		this.starter.writeDeployStatus(DeployStatus.DEPLOYED);

	}

	private void upload() {
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterDeployUrl(remote));
		String root = starter.starterFolder.toURI().getPath();
		List<File> files = FileUtil.loadAllFiles(root);

		for (File file : files) {
			if (file.isFile()) {
				String path = file.toURI().getPath();
				path = path.substring(root.length());
				request.form(path, file);
			}
		}

		HttpResponse response = request.execute();
		String body = response.body();
		this.starter.getLog().info(body);

	}

	public HttpRequest getHttpRequest(String url) {
		HttpRequest request = HttpRequest.post(url);
		request.header("STARTER_TOKEN", starter.token);
		return request;
	}

	public void remove() throws Exception {
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterRemoveUrl(remote));
		HttpResponse response = request.execute();
		String body = response.body();
		this.starter.getLog().info(body);
		this.starter.remove();

	}

	public void start() throws Exception {
		installProject();

		this.starter.writeInstallStatus(InstallStatus.WORK_UPLOADING);
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterStartUrl(remote));

		String root = ((JavaInstall) deployInstall).getRemoteWorkFolder().toURI().getPath();
		List<File> files = FileUtil.loadAllFiles(root);

		for (File file : files) {
			if (file.isFile()) {
				String path = file.toURI().getPath();
				path = path.substring(root.length());
				request.form(path, file);
			}
		}

		HttpResponse response = request.execute();
		String body = response.body();
		this.starter.getLog().info(body);
		this.starter.writeInstallStatus(InstallStatus.WORK_UPLOADED);
	}

	public void stop() {
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterStopUrl(remote));
		HttpResponse response = request.execute();
		String body = response.body();
		this.starter.getLog().info(body);
	}

	public void cleanLog() {
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterCleanLogUrl(remote));
		HttpResponse response = request.execute();
		String body = response.body();
		this.starter.getLog().info(body);
	}

	public JSONObject getStatus() throws Exception {
		String status = this.starter.readDeployStatus();
		if (DeployStatus.DEPLOYED.getValue().equalsIgnoreCase(status)) {
			RemoteBean remote = RemoteHandler.get(remoteid);
			HttpRequest request = getHttpRequest(RemoteHandler.getStarterStatusUrl(remote));
			HttpResponse response = request.execute();
			String body = response.body();
			this.starter.getLog().info(body);

			JSONObject json = JSONObject.parseObject(body);
			json.put("deploy_status", this.starter.readDeployStatus());
			json.put("install_status", this.starter.readInstallStatus());
			return json;
		}
		return (JSONObject) JSON.toJSON(this.starter.getStarterOption());
	}

	@Override
	public JSONObject read(int start, int end, String timestamp) {
		String installStatus = this.starter.readInstallStatus();
		if (InstallStatus.INSTALL_PROJECT_ING.getValue().equalsIgnoreCase(installStatus)
				|| InstallStatus.WORK_UPLOADING.getValue().equalsIgnoreCase(installStatus)) {
			return this.starter.getLog().read(start, end, timestamp);
		}
		String status = this.starter.readDeployStatus();
		if (DeployStatus.DEPLOYED.getValue().equalsIgnoreCase(status)) {
			RemoteBean remote = RemoteHandler.get(remoteid);
			HttpRequest request = getHttpRequest(RemoteHandler.getStarterLogUrl(remote));
			JSONObject param = new JSONObject();
			param.put("start", start);
			param.put("end", end);
			param.put("timestamp", timestamp);

			request.body(param);

			HttpResponse response = request.execute();
			String body = response.body();
			this.starter.getLog().info(body);
			return JSONObject.parseObject(body);
		}

		return this.starter.getLog().read(start, end, timestamp);
	}
}
