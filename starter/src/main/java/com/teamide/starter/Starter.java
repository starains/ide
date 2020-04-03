package com.teamide.starter;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.starter.enums.StarterStatus;
import com.teamide.starter.enums.TerminalEvent;

public class Starter extends StarterParam {

	public Starter(File starterFolder) {
		super(starterFolder);
	}

	public void start() throws Exception {
		writeEvent(TerminalEvent.START.getValue());
	}

	public void stop() throws Exception {
		writeEvent(TerminalEvent.STOP.getValue());
	}

	public void destroy() throws Exception {
		writeEvent(TerminalEvent.DESTROY.getValue());
	}

	public void remove() throws Exception {
		writeStatus(StarterStatus.DESTROYING);
		destroy();
		try {
			Thread.sleep(1000 * 3);
			getLog().remove();
			FileUtils.deleteDirectory(starterFolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cleanLog() {
		log.clean();
	}

	public JSONObject getStarterInfo() {
		JSONObject json = starterJSON;
		if (json != null) {
			json = (JSONObject) json.clone();
			json.put("status", readStatus());
			json.put("deploy_status", readDeployStatus());
			json.put("install_status", readInstallStatus());
		}

		return json;
	}
}
