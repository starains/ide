package com.teamide.protect.ide.processor;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.protect.ide.handler.SpaceHandler;
import com.teamide.protect.ide.processor.param.ProcessorParam;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.param.SpaceProcessorParam;
import com.teamide.protect.ide.util.TokenUtil;
import com.teamide.util.StringUtil;

public class WorkspaceProcessor {

	protected final String token;

	protected final Processor processor;

	public WorkspaceProcessor(ClientSession session, String token) {
		this.token = token;
		JSONObject json = TokenUtil.getJSON(token);
		Processor processor = null;
		String spaceid = null;
		String branch = null;
		if (json != null) {
			spaceid = json.getString("spaceid");
			SpaceBean space = SpaceHandler.get(spaceid);
			if (space != null) {
				if (SpaceHandler.isRepositorys(space)) {
					branch = json.getString("branch");
					if (StringUtil.isEmpty(branch)) {
						branch = "master";
					}
					RepositoryProcessorParam param = new RepositoryProcessorParam(session, spaceid, branch);
					processor = new RepositoryProcessor(param);
				} else {
					SpaceProcessorParam param = new SpaceProcessorParam(session, spaceid);
					processor = new SpaceProcessor(param);
				}
			}
		}
		if (processor == null) {
			ProcessorParam param = new ProcessorParam(session);
			processor = new Processor(param);
		}
		this.processor = processor;
	}

	public Object onDo(String type, JSONObject data) throws Exception {
		return this.processor.onDo(type, data);
	}

	public Object onLoad(String type, JSONObject data) throws Exception {
		return this.processor.onLoad(type, data);
	}
}
