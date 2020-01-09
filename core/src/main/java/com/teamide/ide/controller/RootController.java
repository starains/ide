package com.teamide.ide.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.bean.Status;
import com.teamide.exception.BaseException;
import com.teamide.ide.controller.handler.DataHandler;
import com.teamide.ide.controller.handler.ResourcesHandler;
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

	ResourcesHandler resourcesController = new ResourcesHandler();

	DataHandler dataController = new DataHandler();

	WorkspaceHandler workspaceController = new WorkspaceHandler();

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

				resourcesController.handle(path, request, response);
			} else if (path.startsWith("/api/data/")) {
				dataController.handle(path, request, response);
			} else if (path.startsWith("/api/workspace/")) {
				workspaceController.handle(path, request, response);
			} else {
				if ("GET".equalsIgnoreCase(request.getMethod())) {
					resourcesController.handle("/html/index.html", request, response);
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
