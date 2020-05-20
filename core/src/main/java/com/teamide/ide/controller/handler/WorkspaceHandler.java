package com.teamide.ide.controller.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.ResultBean;
import com.teamide.bean.Status;
import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.ide.exception.NotLoginException;
import com.teamide.ide.processor.WorkspaceProcessor;
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
		String projectPath = null;
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
		if (keys.length > 3) {
			projectPath = path.substring((work + "/" + type + "/" + token + "/").length());
			projectPath = projectPath.substring(1);
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
		data.put("_CLIENT_IP", getIpAddress(request));
		ClientSession session = ClientHandler.getSession(request);
		WorkspaceProcessor workspaceProcessor = new WorkspaceProcessor(session, token, projectPath);
		Object result = null;
		// logger.info("workspace work [" + work + "] type [" + type + "]");

		if (work.equals("do")) {

			if (type.equalsIgnoreCase("LOGIN") || type.equalsIgnoreCase("AUTO_LOGIN") || type.equalsIgnoreCase("LOGOUT")

					|| type.equalsIgnoreCase("INSTALL") || type.equalsIgnoreCase("VALIDATE")

					|| type.equalsIgnoreCase("REGISTER")) {

			} else {
				if (session.getUser() == null) {
					throw new NotLoginException();
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

	private static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ip = inet.getHostAddress();
			}
		}
		return ip;
	}
}
