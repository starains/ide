package com.teamide.ide.processor;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.handler.SpaceHandler;
import com.teamide.ide.param.ProcessorParam;
import com.teamide.ide.param.ProjectProcessorParam;
import com.teamide.ide.param.RepositoryProcessorParam;
import com.teamide.ide.param.SpaceFormatParam;
import com.teamide.ide.param.SpaceProcessorParam;
import com.teamide.ide.util.TokenUtil;
import com.teamide.util.StringUtil;

public class WorkspaceProcessor {

	protected final String token;

	protected final Processor processor;

	protected final ProcessorParam param;

	public WorkspaceProcessor(ClientSession session, String token, String projectPath) {
		this.token = token;
		JSONObject json = TokenUtil.getJSON(token);
		Processor processor = null;
		String spaceid = null;
		String branch = null;
		ProcessorParam param = null;
		if (json != null) {
			spaceid = json.getString("spaceid");
			SpaceBean space = SpaceHandler.get(spaceid);
			SpaceFormatParam spaceFormat = SpaceHandler.getFormat(space, session);
			if (space != null) {
				File spaceRootFolder = SpaceHandler.getSpaceRootFolder();
				if (SpaceHandler.isRepositorys(space)) {
					branch = json.getString("branch");
					if (StringUtil.isEmpty(branch)) {
						branch = "master";
					}
					if (projectPath == null) {
						param = new RepositoryProcessorParam(session, spaceRootFolder, spaceFormat, branch);
						processor = new RepositoryProcessor((RepositoryProcessorParam) param);
					} else {
						param = new ProjectProcessorParam(session, spaceRootFolder, spaceFormat, branch, projectPath);
						processor = new ProjectProcessor((ProjectProcessorParam) param);
					}
				} else {
					param = new SpaceProcessorParam(session, spaceRootFolder, spaceFormat);
					processor = new SpaceProcessor((SpaceProcessorParam) param);
				}
			}
		}
		if (processor == null) {
			param = new ProcessorParam(session);
			processor = new Processor(param);
		}
		this.param = param;
		this.processor = processor;
	}

	public Object onDo(String type, JSONObject data) throws Exception {
		return this.processor.onDo(type, data);
	}

	public Object onLoad(String type, JSONObject data) throws Exception {
		return this.processor.onLoad(type, data);
	}

	public String getToken() {
		return token;
	}

	public Processor getProcessor() {
		return processor;
	}

	public ProcessorParam getParam() {
		return param;
	}
}
