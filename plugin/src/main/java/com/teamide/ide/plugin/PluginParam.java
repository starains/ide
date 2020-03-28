package com.teamide.ide.plugin;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.param.ProjectParam;

public class PluginParam {

	protected final JSONObject option;

	protected final ProjectParam param;

	public PluginParam(ProjectParam param, JSONObject option) {
		this.param = param;
		this.option = option;
	}

	public JSONObject getOption() {
		return option;
	}

	public ProjectParam getParam() {
		return param;
	}

}
