package com.teamide.deploer.server.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.server.ServerListener;
import com.teamide.deploer.server.service.RunnerService;
import com.teamide.deploer.server.service.UploadService;
import com.teamide.deploer.util.StringUtil;

@WebServlet(urlPatterns = "/runner/*")
public class RunnerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final ServerListener listener;

	public RunnerServlet(ServerListener listener) {
		this.listener = listener;
	}

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
		Object result = null;
		switch (pathInfo) {
		case "start":
			result = start(request, response);
			break;
		case "stop":
			result = stop(request, response);
			break;
		case "remove":
			result = remove(request, response);
			break;
		case "status":
			result = status(request, response);
			break;
		case "upload":
			new UploadService().upload(request, response);
			break;
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

	public Object start(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject body = getBody(request);
			String token = body.getString("token");
			JSONObject option = body.getJSONObject("option");
			return new RunnerService().start(token, option);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object stop(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject body = getBody(request);
			String token = body.getString("token");
			JSONObject option = body.getJSONObject("option");
			return new RunnerService().stop(token, option);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object status(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject body = getBody(request);
			String token = body.getString("token");
			JSONObject option = body.getJSONObject("option");
			return new RunnerService().status(token, option);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object remove(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject body = getBody(request);
			String token = body.getString("token");
			JSONObject option = body.getJSONObject("option");
			return new RunnerService().remove(token, option);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getBody(HttpServletRequest request) {
		byte[] bytes = getBytesBody(request);
		if (bytes == null) {
			return null;
		}
		return JSONObject.parseObject(new String(bytes));
	}

	public static byte[] getBytesBody(HttpServletRequest request) {
		InputStream stream = null;
		ByteArrayOutputStream result = null;
		try {
			stream = request.getInputStream();
			if (stream == null) {
				return null;
			}
			result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = stream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (Exception e) {
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
}
