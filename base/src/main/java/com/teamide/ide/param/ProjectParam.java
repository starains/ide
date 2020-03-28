package com.teamide.ide.param;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;

public class ProjectParam extends RepositoryProcessorParam {

	private final String projectPath;

	public ProjectParam(RepositoryProcessorParam param, String projectPath) {
		super(param);

		this.projectPath = projectPath;

	}

	public ProjectParam(ClientSession session, String spaceid, JSONObject formatSpace, String branch,
			String projectPath) {
		super(session, spaceid, formatSpace, branch);

		this.projectPath = projectPath;

	}

	public String getProjectPath() {
		return projectPath;
	}

}
