package com.teamide.deploer.server.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.IDEConstant;

public class UploadService {

	public void upload(HttpServletRequest request, HttpServletResponse response) {

		try {
			// 构造一个文件上传处理对象
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			String tempPath = IDEConstant.TOMCAT_FOLDER + "upload/temp/";
			File fp1 = new File(tempPath);
			if (!fp1.exists())
				fp1.mkdirs();
			factory.setRepository(new File(tempPath));
			factory.setSizeThreshold(10240);

			JSONObject option = new JSONObject();
			String token = null;
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				Iterator<?> items = upload.parseRequest(request).iterator();

				List<FileItem> fileItems = new ArrayList<FileItem>();
				while (items.hasNext()) {
					FileItem item = (FileItem) items.next();
					if (!item.isFormField()) {
						fileItems.add(item);
					} else {
						String name = item.getFieldName();
						if (name.equals("token")) {
							token = item.getString();
						} else if (name.equals("option")) {
							option = JSONObject.parseObject(item.getString());
						}
					}
				}
				File root = RunnerService.getRoot(token, option);

				for (FileItem item : fileItems) {
					String path = item.getFieldName();
					File file = new File(root, path);
					if (file.exists()) {
						file.delete();
					} else {
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
					}
					file.createNewFile();
					FileOutputStream outputStream = new FileOutputStream(file);
					InputStream inputStream = item.getInputStream();
					try {

						int read = 0;

						byte[] b = new byte[1024];
						while ((read = inputStream.read(b)) != -1) {
							outputStream.write(b, 0, read);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						if (outputStream != null) {
							outputStream.flush();
							outputStream.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
