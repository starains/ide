package com.teamide.ide.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.ide.plugin.IDEListener;
import com.teamide.ide.plugin.IDEPlugin;
import com.teamide.ide.plugin.PluginHandler;
import com.teamide.util.StringUtil;

public class PluginResourcesHandler {

	public static String PATH_PREFIX = "/api/plugin/resources/";

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (path.startsWith(PATH_PREFIX)) {
			path = path.substring(PATH_PREFIX.length());
		}
		String[] keys = path.split("/");
		String name = null;
		String version = null;
		String resourceName = null;
		if (keys.length > 0) {
			name = keys[0];
		}
		if (keys.length > 1) {
			version = keys[1];
		}
		if (keys.length > 2) {
			resourceName = path.substring((name + "/" + version + "/").length());
		}
		if (StringUtil.isEmpty(name)) {
			throw new Exception("name is null.");
		}
		if (StringUtil.isEmpty(version)) {
			throw new Exception("version is null.");
		}

		IDEPlugin plugin = PluginHandler.getPlugin(name, version);
		if (plugin != null && plugin.getListener() != null) {
			IDEListener listener = plugin.getListener();

			listener.onResources(resourceName, request, response);
		}
	}

}
