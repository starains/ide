package com.teamide.deploer.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.teamide.bean.Status;
import com.teamide.deploer.starter.RemoteService;
import com.teamide.util.StringUtil;

@WebServlet(urlPatterns = "/remote/*")
public class RemoteServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (StringUtil.isEmpty(pathInfo)) {
			pathInfo = "";
		}
		if (pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1);
		}

		Object result = Status.SUCCESS();
		try {

			switch (pathInfo) {
			case "start":
				result = Status.SUCCESS();
				break;
			case "stop":
				result = Status.SUCCESS();
				break;
			case "status":
				result = Status.SUCCESS();
				break;
			case "log":
				result = Status.SUCCESS();
				break;
			case "check":
				result = Status.SUCCESS();
				break;
			case "plugins":
				new RemoteService().plugins(request);
				break;
			}
		} catch (Exception e) {
			Status status = Status.FAIL();
			status.setErrmsg(e.getMessage());
			result = status;
		}
		if (result != null) {

			response.setHeader("Content-type", "text/json;charset=UTF-8");
			PrintWriter printWriter;
			response.setCharacterEncoding("UTF-8");
			printWriter = null;
			try {
				printWriter = response.getWriter();
				printWriter.print(JSON.toJSON(result));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (printWriter != null)
					printWriter.close();
			}

		}
	}

}
