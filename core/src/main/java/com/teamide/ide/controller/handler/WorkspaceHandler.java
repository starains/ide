package com.teamide.ide.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.ResultBean;
import com.teamide.bean.Status;
import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.protect.ide.processor.WorkspaceProcessor;
import com.teamide.util.LogUtil;
import com.teamide.util.RequestUtil;
import com.teamide.util.ResponseUtil;
import com.teamide.util.StringUtil;

public class WorkspaceHandler {

	Logger logger = LogUtil.get();

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String body = RequestUtil.getBody(request);
		JSONObject data = new JSONObject();
		if (StringUtil.isNotEmpty(body)) {
			data = JSONObject.parseObject(body);
		}

		String work = null;
		String type = null;
		String token = null;
		if (path.startsWith("/api/workspace/")) {
			path = path.substring("/api/workspace/".length());
		}
		String[] keys = path.split("/");
		if (keys.length > 0) {
			work = keys[0];
		}
		if (keys.length > 1) {
			type = keys[1];
		}
		if (keys.length > 2) {
			token = keys[2];
		}
		if (StringUtil.isEmpty(work)) {
			throw new Exception("work is null.");
		}
		if (StringUtil.isEmpty(type)) {
			throw new Exception("type is null.");
		}
		if (StringUtil.isEmpty(token)) {
			throw new Exception("token is null.");
		}

		ClientSession session = ClientHandler.getSession(request);
		WorkspaceProcessor workspaceProcessor = new WorkspaceProcessor(session, token);
		Object result = null;
		// logger.info("workspace work [" + work + "] type [" + type + "]");

		if (work.equals("do")) {

			if (type.equalsIgnoreCase("LOGIN") || type.equalsIgnoreCase("LOGOUT")

					|| type.equalsIgnoreCase("INSTALL") || type.equalsIgnoreCase("VALIDATE")

					|| type.equalsIgnoreCase("REGISTER")) {

			} else {
				if (session.getUser() == null) {
					throw new Exception("登录信息丢失，请先登录！");
				}
			}

			result = workspaceProcessor.onDo(type, data);
		} else if (work.equals("load")) {
			result = workspaceProcessor.onLoad(type, data);
		}
		if (result == null) {
			result = Status.SUCCESS();
		}
		if (result instanceof ResultBean) {

		} else {
			Status status = Status.SUCCESS();
			status.setValue(result);
			result = status;
		}
		ResponseUtil.outJSON(response, result);
	}

}
