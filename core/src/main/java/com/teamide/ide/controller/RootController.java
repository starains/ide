package com.teamide.ide.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.Status;
import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.exception.BaseException;
import com.teamide.ide.controller.handler.DataHandler;
import com.teamide.ide.controller.handler.DownloadHandler;
import com.teamide.ide.controller.handler.ResourcesHandler;
import com.teamide.ide.controller.handler.ResourcesMergeHandler;
import com.teamide.ide.controller.handler.ListenHandler;
import com.teamide.ide.controller.handler.WorkspaceHandler;
import com.teamide.util.RequestUtil;
import com.teamide.util.ResponseUtil;
import com.teamide.util.StringUtil;

@WebServlet(value = "/*")
public class RootController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4357127201255115763L;

	ResourcesHandler resourcesHandler = new ResourcesHandler();

	ResourcesMergeHandler resourcesMergeHandler = new ResourcesMergeHandler();

	DataHandler dataHandler = new DataHandler();

	ListenHandler listenHandler = new ListenHandler();

	WorkspaceHandler workspaceHandler = new WorkspaceHandler();

	DownloadHandler downloadHandler = new DownloadHandler();

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String servletpath = RequestUtil.getServletpath(request);
		int lastIndex = servletpath.lastIndexOf("$ROOT$");
		if (lastIndex >= 0) {
			servletpath = servletpath.substring(lastIndex + ("$ROOT$").length());

		}
		handle(servletpath, request, response);
	}

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (StringUtil.isEmpty(request.getPathInfo())) {
				String contextPath = request.getContextPath();
				if (!contextPath.startsWith("/")) {
					contextPath = "/" + contextPath;
				}
				if (!contextPath.endsWith("/")) {
					contextPath += "/";
				}
				response.sendRedirect(contextPath);
				return;
			}
			if (path.startsWith("/resources/") || path.startsWith("/html/")

					|| path.endsWith(".js")

					|| path.endsWith(".css")

					|| path.endsWith(".html")) {
				if (path.startsWith("/resources/coos/merge/")) {
					resourcesMergeHandler.handle(path, request, response);
				} else {
					resourcesHandler.handle(path, request, response);
				}
			} else if (path.startsWith("/api/data/")) {
				dataHandler.handle(path, request, response);
			} else if (path.startsWith("/api/listen")) {
				listenHandler.listen(request, response);
			} else if (path.startsWith("/api/validate")) {
				Status status = Status.SUCCESS();
				ClientSession session = ClientHandler.getSession(request);
				JSONObject json = new JSONObject();
				if (session.isLogin()) {
					json.put("isLogin", true);
				}
				status.setValue(json);
				ResponseUtil.outJSON(response, status);
			} else if (path.startsWith("/api/workspace/")) {
				workspaceHandler.handle(path, request, response);
			} else if (path.startsWith("/api/download/")) {
				downloadHandler.handle(path, request, response);
			} else {
				if ("GET".equalsIgnoreCase(request.getMethod())) {
					resourcesHandler.handle("/html/index.html", request, response);
				} else {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Status status = new Status();
			if (e instanceof BaseException) {
				BaseException baseException = (BaseException) e;
				status.setErrcode(baseException.getErrcode());
				status.setErrmsg(baseException.getErrmsg());
			} else {
				status.setErrcode(-1);
				status.setErrmsg(e.getMessage());
			}
			ResponseUtil.outJSON(response, status);
		}
	}

}
