package com.teamide.ide.plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

public interface IDEListener {

	public void onResources(String name, HttpServletRequest request, HttpServletResponse response);

	public void onServletEvent(PluginParam param, String event, HttpServletRequest request,
			HttpServletResponse response);

	public Object onDoEvent(PluginParam param, String event, JSONObject data);

	public Object onLoadEvent(PluginParam param, String event, JSONObject data);

}
