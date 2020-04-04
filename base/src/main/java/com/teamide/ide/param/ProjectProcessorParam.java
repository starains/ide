package com.teamide.ide.param;

import java.io.File;

import com.teamide.client.ClientSession;

public class ProjectProcessorParam extends RepositoryProcessorParam {

	private final String projectPath;

	public ProjectProcessorParam(RepositoryProcessorParam param, String projectPath) {
		super(param);

		this.projectPath = projectPath;

	}

	public ProjectProcessorParam(ClientSession session, File spaceRootFolder, SpaceFormatParam spaceFormat,
			String branch, String projectPath) {
		super(session, spaceRootFolder, spaceFormat, branch);

		this.projectPath = projectPath;

	}

	public String getProjectPath() {
		return projectPath;
	}

}
