package com.teamide.ide.controller.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.ide.processor.WorkspaceProcessor;
import com.teamide.util.FileUtil;
import com.teamide.util.LogUtil;
import com.teamide.util.StringUtil;

public class DownloadHandler {

	Logger logger = LogUtil.get();

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject data = new JSONObject();
		data.put("path", request.getParameter("path"));

		String token = null;
		if (path.startsWith("/api/download/")) {
			token = path.substring("/api/download/".length());
		}
		if (StringUtil.isEmpty(token)) {
			throw new Exception("token is null.");
		}

		ClientSession session = ClientHandler.getSession(request);
		WorkspaceProcessor workspaceProcessor = new WorkspaceProcessor(session, token);

		File file = (File) workspaceProcessor.onDo("FILE_DOWNLOAD", data);
		if (file != null && file.exists() && file.isFile()) {
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
			// response.setContentType("application/text");
			response.setCharacterEncoding("UTF-8");

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				response.getOutputStream().write(FileUtil.read(file));
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			} finally {
				bos.close();
			}
		}
	}

}
