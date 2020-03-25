package com.teamide.ide.deployer;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.enums.InstallStatus;

public abstract class Deploy extends DeployParam {

	public Deploy(File starterFolder) {
		super(starterFolder);
	}

	public abstract void remove() throws Exception;

	public abstract void start() throws Exception;

	public abstract void stop() throws Exception;

	public abstract void cleanLog() throws Exception;

	public abstract void deploy() throws Exception;

	public void install() throws Exception {
		try {
			installServer();
		} catch (Exception e) {
			e.printStackTrace();
			this.starter.getLog().error(e.getMessage());
			this.starter.writeInstallStatus(InstallStatus.INSTALL_SERVER_ERROR);
		}

	}

	protected void installServer() throws Exception {

		this.starter.writeInstallStatus(InstallStatus.INSTALL_SERVER);
		this.starter.getLog().info("starter install server");
		if (this.starter.starterServerFolder.exists()) {
			FileUtils.deleteDirectory(this.starter.starterServerFolder);
		}
		File server = deployInstall.getServer();
		if (server != null && server.exists()) {
			this.starter.starterServerFolder.mkdirs();
			FileUtils.copyDirectory(server, this.starter.starterServerFolder);
		}
	}

	protected void installProject() throws Exception {
		this.starter.writeInstallStatus(InstallStatus.INSTALL_PROJECT_ING);
		doCompile();
		this.starter.writeInstallStatus(InstallStatus.INSTALL_PROJECT_ED);
	}

	protected void doCompile() throws Exception {
		deployInstall.compile();
	}

	public abstract JSONObject getStatus() throws Exception;

	public abstract JSONObject read(int start, int end, String timestamp);
}
