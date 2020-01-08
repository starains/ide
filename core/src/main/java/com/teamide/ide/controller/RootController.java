package com.teamide.ide.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.bean.Status;
import com.teamide.exception.BaseException;
import com.teamide.util.RequestUtil;
import com.teamide.util.ResponseUtil;

@WebServlet(value = "/*")
public class RootController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4357127201255115763L;

	ResourcesController resourcesController = new ResourcesController();

	DataController dataController = new DataController();

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

			if (path.startsWith("/resources/")

					|| path.endsWith(".js")

					|| path.endsWith(".css")

					|| path.endsWith(".html")) {

				resourcesController.handle(path, request, response);
			} else if (path.startsWith("/api/data/")) {
				dataController.handle(path, request, response);
			} else {
				System.out.println(path);
			}
		} catch (Exception e) {
			Status status = new Status();
			if (e instanceof BaseException) {
				BaseException baseException = (BaseException) e;
				status.setErrcode(baseException.getErrcode());
				status.setErrcode(baseException.getErrmsg());
			} else {
				status.setErrcode(-1);
				status.setErrcode(e.getMessage());
			}
			ResponseUtil.outJSON(response, status);
		}
	}

}
