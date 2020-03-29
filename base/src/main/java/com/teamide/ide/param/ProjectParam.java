package com.teamide.ide.param;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;

public class ProjectParam extends RepositoryProcessorParam {

	private final String projectPath;

	public ProjectParam(RepositoryProcessorParam param, String projectPath) {
		super(param);

		this.projectPath = projectPath;

	}

	public ProjectParam(ClientSession session, File spaceRootFolder, JSONObject formatSpace, String branch,
			String projectPath) {
		super(session, spaceRootFolder, formatSpace, branch);

		this.projectPath = projectPath;

	}

	public String getProjectPath() {
		return projectPath;
	}

}
