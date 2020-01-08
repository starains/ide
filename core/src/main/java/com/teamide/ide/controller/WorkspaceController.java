package com.teamide.ide.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.protect.ide.processor.WorkspaceProcessor;
import com.teamide.util.RequestUtil;
import com.teamide.util.StringUtil;

@WebServlet(value = "/api/workspace/*")
public class WorkspaceController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7322388753372157693L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// String servletpath = RequestUtil.getServletpath(request);
		// handle(servletpath, request, response);
	}

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String token = request.getParameter("token");
		String body = RequestUtil.getBody(request);
		JSONObject data = new JSONObject();
		if (StringUtil.isNotEmpty(body)) {
			data = JSONObject.parseObject(body);
		}
		ClientSession client = ClientHandler.getSession(request);
		WorkspaceProcessor workspaceProcessor = new WorkspaceProcessor(client, token);
		String type = request.getParameter("type");
		if (path.endsWith("do")) {
			workspaceProcessor.onDo(type, data);
		} else if (path.endsWith("load")) {

		}
	}

}
