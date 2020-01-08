package com.teamide.ide.enums;

import com.teamide.ide.bean.WorkspaceDataBean;

public enum WorkspaceData {

	RUN_OPTIONS(null),

	TERMINALS(null),

	TERMINAL_STATUS(null),

	TERMINAL_LOG(null),

	RUNNER_SERVERS(null),

	RUNNER_CLIENTS(null),

	RUNNERS(null),

	RUNNER_OPTIONS(null),

	RUNNER_STATUS(null),

	RUNNER_LOG(null),

	REPOSITORY(null),

	PROJECT(null),

	APP(null),

	GIT(null),

	GIT_BRANCH_LIST(null),

	GIT_LOG(null),

	GIT_REMOTE_LIST(null),

	GIT_STATUS(null),

	FILE_OPENS(null),

	;

	private WorkspaceData(WorkspaceDataBean data) {

		this.data = data;
	}

	private final WorkspaceDataBean data;

	public WorkspaceDataBean getData() {

		return data;
	}

}
