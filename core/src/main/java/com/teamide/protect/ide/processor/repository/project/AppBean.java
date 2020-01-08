package com.teamide.protect.ide.processor.repository.project;

import com.alibaba.fastjson.JSONObject;
import com.teamide.protect.ide.bean.AppOption;

public class AppBean {

	private String path;
	private String localpath;
	private JSONObject path_model_type;
	private JSONObject path_model_bean;

	private AppOption option;

	public final JSONObject context = new JSONObject();

	public AppOption getOption() {
		return option;
	}

	public void setOption(AppOption option) {
		this.option = option;
	}

	public JSONObject getContext() {
		return context;
	}

	public AppBean setContext(String key, Object value) {
		context.put(key, value);
		return this;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLocalpath() {
		return localpath;
	}

	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}

	public JSONObject getPath_model_type() {
		return path_model_type;
	}

	public void setPath_model_type(JSONObject path_model_type) {
		this.path_model_type = path_model_type;
	}

	public JSONObject getPath_model_bean() {
		return path_model_bean;
	}

	public void setPath_model_bean(JSONObject path_model_bean) {
		this.path_model_bean = path_model_bean;
	}

}
