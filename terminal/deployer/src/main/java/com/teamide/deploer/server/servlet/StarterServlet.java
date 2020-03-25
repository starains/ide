package com.teamide.deploer.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.Status;
import com.teamide.deploer.starter.StarterService;
import com.teamide.util.RequestUtil;
import com.teamide.util.StringUtil;

@WebServlet(urlPatterns = "/starter/*")
public class StarterServlet extends HttpServlet {

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
		String token = request.getHeader("STARTER_TOKEN");
		StarterService starterService = new StarterService(token);
		Object result = Status.SUCCESS();
		try {
			switch (pathInfo) {
			case "start":
				starterService.start(request);
				break;
			case "stop":
				starterService.stop();
				break;
			case "remove":
				starterService.remove();
				break;
			case "status":
				result = starterService.status();
				break;
			case "log":
				String body = RequestUtil.getBody(request);
				JSONObject json = JSONObject.parseObject(body);
				result = starterService.log(json.getIntValue("start"), json.getIntValue("end"),
						json.getString("timestamp"));
				break;
			case "cleanLog":
				starterService.cleanLog();
				break;
			case "deploy":
				starterService.deploy(request);
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
