package com.teamide.ide.deployer;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

public class LocalDeploy extends Deploy {

	public LocalDeploy(File starterFolder) {
		super(starterFolder);
	}

	public void deploy() {
		this.starter.writeDeployStatus("DEPLOYED");
	}

	public void remove() {
		this.starter.remove();
	}

	public void destroy() {
		this.starter.destroy();
	}

	public void start() throws Exception {
		install();
		checkStartStarter();
		this.starter.start();
	}

	public void stop() {
		this.starter.stop();
	}

	public void cleanLog() {
		this.starter.cleanLog();
	}

	public JSONObject getStatus() {
		return this.starter.getStarterInfo();
	}

	@Override
	public JSONObject read(int start, int end, String timestamp) {
		return this.starter.getLog().read(start, end, timestamp);
	}
}
