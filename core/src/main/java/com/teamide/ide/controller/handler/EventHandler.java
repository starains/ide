package com.teamide.ide.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.ide.param.ProjectParam;
import com.teamide.ide.plugin.IDEListener;
import com.teamide.ide.plugin.IDEPlugin;
import com.teamide.ide.plugin.PluginHandler;
import com.teamide.ide.plugin.PluginParam;
import com.teamide.ide.processor.WorkspaceProcessor;
import com.teamide.util.StringUtil;

public class EventHandler {

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (path.startsWith("/api/event/")) {
			path = path.substring("/api/event/".length());
		}
		String[] keys = path.split("/");
		String name = null;
		String version = null;
		String event = null;
		String token = null;
		String projectPath = null;
		if (keys.length > 0) {
			name = keys[0];
		}
		if (keys.length > 1) {
			version = keys[1];
		}
		if (keys.length > 2) {
			event = keys[2];
		}
		if (keys.length > 3) {
			token = keys[3];
		}
		if (keys.length > 4) {
			projectPath = keys[4].replace("project-", "");

		}
		if (StringUtil.isEmpty(name)) {
			throw new Exception("name is null.");
		}
		if (StringUtil.isEmpty(version)) {
			throw new Exception("version is null.");
		}
		if (StringUtil.isEmpty(event)) {
			throw new Exception("event is null.");
		}
		if (StringUtil.isEmpty(token)) {
			throw new Exception("token is null.");
		}

		ClientSession session = ClientHandler.getSession(request);

		WorkspaceProcessor processor = new WorkspaceProcessor(session, token, projectPath);

		if (processor.getParam() != null && processor.getParam() instanceof ProjectParam) {
			IDEPlugin plugin = PluginHandler.getPlugin(name, version);
			if (plugin != null && plugin.getListener() != null) {
				IDEListener listener = plugin.getListener();
				ProjectParam param = (ProjectParam) processor.getParam();
				PluginParam pluginParam = PluginHandler.getParam(param, plugin);
				listener.onServletEvent(pluginParam, event, request, response);
			}
		}

	}

}
