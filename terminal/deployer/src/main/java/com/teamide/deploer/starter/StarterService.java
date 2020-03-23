package com.teamide.deploer.starter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.IDEConstant;

public class StarterService {

	protected final String token;

	protected final File starterFolder;

	public StarterService(String token) {
		this.token = token;
		File folder = new File(IDEConstant.WORKSPACES_STARTER_FOLDER);
		this.starterFolder = new File(folder, token);
	}

	public JSONObject status() throws Exception {
		Starter starter = new Starter(starterFolder);
		return starter.getStarterInfo();
	}

	public JSONObject log(int start, int end, String timestamp) throws Exception {
		Starter starter = new Starter(starterFolder);
		return starter.getLog().read(start, end, timestamp);
	}

	public void stop() throws Exception {
		Starter starter = new Starter(starterFolder);
		starter.stop();
	}

	public void destroy() throws Exception {
		Starter starter = new Starter(starterFolder);
		starter.destroy();
	}

	public void cleanLog() throws Exception {
		Starter starter = new Starter(starterFolder);
		starter.cleanLog();
	}

	public void remove() throws Exception {
		Starter starter = new Starter(starterFolder);
		starter.remove();
	}

	public void checkStartStarter() throws Exception {
		Starter starter = new Starter(starterFolder);
		boolean stoped = true;
		if (starter.readTimestamp() < System.currentTimeMillis() - (1000 * 5)) {
			stoped = false;
		}
		if (!stoped) {
			starter.startStarter(null, null);

		}
	}

	public void start() throws Exception {
		checkStartStarter();

		Starter starter = new Starter(starterFolder);

		starter.start();
	}

	public void deploy(HttpServletRequest request) throws Exception {
		// 构造一个文件上传处理对象
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		String tempPath = IDEConstant.TOMCAT_FOLDER + "upload/temp/";
		File fp1 = new File(tempPath);
		if (!fp1.exists())
			fp1.mkdirs();
		factory.setRepository(new File(tempPath));
		factory.setSizeThreshold(10240);

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			Iterator<?> items = upload.parseRequest(request).iterator();

			List<FileItem> fileItems = new ArrayList<FileItem>();
			while (items.hasNext()) {
				FileItem item = (FileItem) items.next();
				if (!item.isFormField()) {
					fileItems.add(item);
				} else {
				}
			}

			for (FileItem item : fileItems) {
				String path = item.getFieldName();
				File file = new File(this.starterFolder, path);
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
	}

}
