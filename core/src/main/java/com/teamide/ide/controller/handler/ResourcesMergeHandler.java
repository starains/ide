package com.teamide.ide.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.util.ResponseUtil;

public class ResourcesMergeHandler {
	ResourceMergeService mergeService = new ResourceMergeService();

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) {

		if (path.endsWith("/coos.js")) {
			String content = mergeService.getJS().toString();
			ResponseUtil.outJS(response, content);
		} else if (path.endsWith("/coos.css")) {
			String content = mergeService.getCSS().toString();
			ResponseUtil.outCSS(response, content);
		} else if (path.endsWith("/editor.js")) {
			String content = mergeService.getEditorJS().toString();
			ResponseUtil.outJS(response, content);
		} else if (path.endsWith("/editor.css")) {
			String content = mergeService.getEditorCSS().toString();
			ResponseUtil.outCSS(response, content);
		} else if (path.endsWith("/page.editor.js")) {
			String content = mergeService.getPageEditorJS().toString();
			ResponseUtil.outJS(response, content);
		} else if (path.endsWith("/page.editor.css")) {
			String content = mergeService.getPageEditorCSS().toString();
			ResponseUtil.outCSS(response, content);
		}
	}

}