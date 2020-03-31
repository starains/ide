package com.teamide.ide.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.util.ResponseUtil;

public class ResourcesPluginMergeHandler {
	ResourcePluginMergeService mergeService = new ResourcePluginMergeService();

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) {
		if (path.startsWith("/resources/plugin/merge/")) {
			path = path.substring("/resources/plugin/merge/".length());
		}
		String[] keys = path.split("/");
		String name = null;
		String version = null;
		if (keys.length > 0) {
			name = keys[0];
		}
		if (keys.length > 1) {
			version = keys[1];
		}
		if (path.endsWith(".js")) {
			String content = mergeService.getJS(name, version).toString();
			ResponseUtil.outJS(response, content);
		} else if (path.endsWith(".css")) {
			String content = mergeService.getCSS(name, version).toString();
			ResponseUtil.outCSS(response, content);
		}
	}

}