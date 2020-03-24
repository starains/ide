package com.teamide.ide.deployer;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.enums.DeployStatus;
import com.teamide.deploer.enums.InstallStatus;
import com.teamide.http.HttpRequest;
import com.teamide.http.HttpResponse;
import com.teamide.ide.bean.RemoteBean;
import com.teamide.ide.handler.RemoteHandler;
import com.teamide.util.FileUtil;

public class RemoteDeploy extends Deploy {

	private final String remoteid;

	public RemoteDeploy(String remoteid, File starterFolder) {
		super(starterFolder);
		this.remoteid = remoteid;
	}

	public void deploy() throws Exception {
		this.starter.writeDeployStatus(DeployStatus.UPLOADING);
		install();
		upload();
		this.starter.writeDeployStatus(DeployStatus.UPLOADED);

		this.starter.writeDeployStatus(DeployStatus.DEPLOYED);
	}

	private void upload() {
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterDeployUrl(remote));
		String root = starter.starterFolder.getAbsolutePath();
		List<File> files = FileUtil.loadAllFiles(root);

		for (File file : files) {
			if (file.isFile()) {
				String path = file.getAbsolutePath();
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

	public void remove() {
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterRemoveUrl(remote));
		HttpResponse response = request.execute();
		String body = response.body();
		this.starter.getLog().info(body);
		this.starter.remove();

	}

	public void destroy() {
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterDestroyUrl(remote));
		HttpResponse response = request.execute();
		String body = response.body();
		this.starter.getLog().info(body);
	}

	public void start() throws Exception {
		installProject();

		this.starter.writeInstallStatus(InstallStatus.WORK_UPLOADING);
		RemoteBean remote = RemoteHandler.get(remoteid);
		HttpRequest request = getHttpRequest(RemoteHandler.getStarterStartUrl(remote));

		String root = starter.workFolder.getAbsolutePath();
		List<File> files = FileUtil.loadAllFiles(root);

		for (File file : files) {
			if (file.isFile()) {
				String path = file.getAbsolutePath();
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
		if (DeployStatus.UPLOADED.getValue().equalsIgnoreCase(status)
				|| DeployStatus.DEPLOYED.getValue().equalsIgnoreCase(status)) {
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
		return this.starter.getStarterInfo();
	}

	@Override
	public JSONObject read(int start, int end, String timestamp) {
		String status = this.starter.readDeployStatus();
		if (DeployStatus.UPLOADED.getValue().equalsIgnoreCase(status)
				|| DeployStatus.DEPLOYED.getValue().equalsIgnoreCase(status)) {
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
