package com.teamide.ide.processor.repository;

import com.teamide.LogTool;
import com.teamide.ide.param.RepositoryProcessorParam;

public class RepositoryBase {

	public final RepositoryProcessorParam param;

	public RepositoryBase(RepositoryProcessorParam param) {

		this.param = param;
	}

	public LogTool getLog() {

		return LogTool.get(param.getLogName(), param.getLogFolder());
	}

	public LogTool getDeployerLog(String token) {

		return LogTool.get(param.getDeployerLogName(token), param.getDeployerLogFolder());
	}

}
