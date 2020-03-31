package com.teamide.ide.processor.repository;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.Status;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.param.ProjectParam;
import com.teamide.ide.plugin.PluginHandler;
import com.teamide.ide.util.ZipUtil;
import com.teamide.util.FileUtil;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.ResponseUtil;
import com.teamide.util.StringUtil;

public class RepositoryFile extends RepositoryBase {

	public final ProjectParam param;

	public RepositoryFile(ProjectParam param) {
		super(param);
		this.param = param;
	}

	public File callChange(final File folder) {

		new Thread() {

			@Override
			public void run() {
				try {
					// ProjectLoader loader = new ProjectLoader(param);
					// final String path = param.getPath(folder);
					// ProjectBean projectBean = loader.getProjectByPath(path);
					// if (projectBean != null) {
					// File projectFolader =
					// param.getFile(projectBean.getPath());
					// List<Starter> starters =
					// StarterHandler.getStarters(param);
					// for (Starter starter : starters) {
					// String path_ =
					// starter.getStarterInfo().getString("path");
					// File terminalProjectFolder = param.getFile(path_);
					// if
					// (projectFolader.getAbsolutePath().equals(terminalProjectFolder.getAbsolutePath()))
					// {
					// starter.onChange(folder);
					// }
					// }
					//
					// }

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();

		return folder;
	}

	public File paste(final String path, final String source) throws Exception {

		this.getLog().info("file paste,  path:" + path + ",  source:" + source);
		File sourceFile = this.param.getFile(source);
		if (!sourceFile.exists()) {
			throw new Exception("源文件不存在！");
		}
		File file = this.param.getFile(path);

		if (!this.param.sourceContains(sourceFile)) {
			throw new Exception(source + " is not in the source directory.");
		}
		if (!this.param.sourceContains(file)) {
			throw new Exception(path + " is not in the source directory.");
		}

		if (file.exists()) {
			throw new Exception("文件已存在！");
		}
		File parent = file.getParentFile();
		List<File> not_exists = new ArrayList<File>();
		while (parent != null && !parent.exists()) {
			not_exists.add(parent);
			parent = parent.getParentFile();

		}
		for (int i = not_exists.size() - 1; i >= 0; i--) {
			File not_exist = not_exists.get(i);
			if (not_exist.exists()) {
				continue;
			}
			not_exist.mkdir();
		}
		if (sourceFile.isFile()) {
			FileUtils.copyFile(sourceFile, file);

		} else {
			FileUtils.copyDirectory(sourceFile, file);
		}

		File folder = file.getParentFile();

		if (not_exists.size() > 0) {
			folder = not_exists.get(not_exists.size() - 1).getParentFile();
		}

		return callChange(folder);
	}

	public File save(final String path, String content) throws Exception {

		this.getLog().info("file save,  path:" + path);

		if (content == null) {
			content = "";
		}
		File file = this.param.getFile(path);
		if (!this.param.sourceContains(file)) {
			throw new Exception(path + " is not in the source directory.");
		}
		FileUtil.write(content.getBytes(), file);
		return callChange(file.getParentFile());

	}

	public File rename(final String path, final String name) throws Exception {

		this.getLog().info("file rename,  path:" + path + ",  name:" + name);
		if (StringUtil.isEmpty(name)) {
			throw new Exception("文件名称不能为空！");
		}
		File oldFile = this.param.getFile(path);
		if (!oldFile.exists()) {
			throw new Exception("文件[" + oldFile.getName() + "]丢失！");
		}
		File newFile = new File(oldFile.getParentFile(), name);
		if (newFile.exists()) {
			throw new Exception("文件[" + newFile.getName() + "]已存在！");
		}
		if (!this.param.sourceContains(oldFile)) {
			throw new Exception(path + " is not in the source directory.");
		}
		if (!this.param.sourceContains(newFile)) {
			throw new Exception(name + " is not in the source directory.");
		}

		File parent = newFile.getParentFile();
		List<File> not_exists = new ArrayList<File>();
		while (parent != null && !parent.exists()) {
			not_exists.add(parent);
			parent = parent.getParentFile();

		}
		for (int i = not_exists.size() - 1; i >= 0; i--) {
			File not_exist = not_exists.get(i);
			if (not_exist.exists()) {
				continue;
			}
			not_exist.mkdir();
		}

		File folder = newFile.getParentFile();
		if (not_exists.size() > 0) {
			folder = not_exists.get(not_exists.size() - 1).getParentFile();
		}
		List<File> files = null;
		if (oldFile.isDirectory()) {
			files = FileUtil.loadAllFiles(oldFile.getAbsolutePath());
		}
		if (oldFile.getParent().equals(newFile.getParent())) {
			oldFile.renameTo(newFile);
		} else {
			FileUtil.write(FileUtil.read(oldFile), newFile);
			FileUtils.forceDelete(oldFile);
		}
		if (files == null) {
			PluginHandler.moveFile(param, oldFile, newFile);
		} else {
			String oldPath = oldFile.getAbsolutePath();
			String newPath = newFile.getAbsolutePath();
			for (File f : files) {
				String fPath = f.getAbsolutePath();
				fPath = fPath.replace(oldPath, newPath);
				File toF = new File(fPath);
				if (toF.exists() && toF.isFile()) {
					PluginHandler.moveFile(param, f, toF);
				}
			}
		}
		return callChange(folder);
	}

	public File create(final String parentPath, String name, boolean isFile, String content) throws Exception {

		this.getLog().info("file create,  parentPath:" + parentPath + ",  name:" + name);
		String path = parentPath + "/" + name;
		if (StringUtil.isEmpty(parentPath)) {
			path = name;
		}
		File file = this.param.getFile(path);
		if (!this.param.sourceContains(file)) {
			throw new Exception(name + " is not in the source directory.");
		}
		if (file.exists()) {
			if (isFile) {
				throw new Exception("文件[" + name + "]已存在.");
			} else {
				throw new Exception("文件夹[" + name + "]已存在.");
			}
		}
		File parent = file.getParentFile();
		List<File> not_exists = new ArrayList<File>();
		while (parent != null && !parent.exists()) {
			not_exists.add(parent);
			parent = parent.getParentFile();

		}
		for (int i = not_exists.size() - 1; i >= 0; i--) {
			File not_exist = not_exists.get(i);
			if (not_exist.exists()) {
				continue;
			}
			not_exist.mkdir();
		}

		if (isFile) {
			file.createNewFile();
			if (!StringUtil.isEmpty(content)) {
				FileUtil.write(content.getBytes(), file);
			}
		} else {
			file.mkdir();
		}

		return callChange(parent);

	}

	public File delete(final String path) throws Exception {

		this.getLog().info("file delete,  path:" + path);

		File file = this.param.getFile(path);
		if (!this.param.sourceContains(file)) {
			throw new Exception(path + " is not in the source directory.");
		}
		File folder = file.getParentFile();
		if (file.exists()) {
			if (file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				FileUtils.forceDelete(file);
			}
			folder = callChange(file.getParentFile());
		}
		return folder;
	}

	public File move(final String path, final String to) throws Exception {

		this.getLog().info("file move,  path:" + path + ",  to:" + to);

		File file = this.param.getFile(path);
		if (!this.param.sourceContains(file)) {
			throw new Exception(path + " is not in the source directory.");
		}
		File toFolder = this.param.getFile(to);
		File toFile = new File(toFolder, file.getName());
		if (!this.param.sourceContains(toFile)) {
			throw new Exception(this.param.getPath(toFile) + " is not in the source directory.");
		}
		if (toFile.exists()) {
			throw new Exception(this.param.getPath(toFile) + " existed.");
		}
		File folder = toFolder;
		if (file.exists()) {
			if (!toFolder.exists()) {
				toFolder.mkdirs();
			}
			if (file.isFile()) {
				FileUtils.moveFileToDirectory(file, toFolder, false);
				PluginHandler.moveFile(param, file, toFile);
			} else {
				List<File> files = FileUtil.loadAllFiles(file.getAbsolutePath());
				FileUtils.moveDirectoryToDirectory(file, toFolder, false);
				String oldPath = file.getAbsolutePath();
				String newPath = toFile.getAbsolutePath();
				for (File f : files) {
					String fPath = f.getAbsolutePath();
					fPath = fPath.replace(oldPath, newPath);
					File toF = new File(fPath);
					if (toF.exists() && toF.isFile()) {
						PluginHandler.moveFile(param, f, toF);
					}
				}
			}
			folder = callChange(toFolder);
		}
		return folder;
	}

	public void download(JSONObject data) throws Exception {
		HttpServletRequest request = (HttpServletRequest) data.get("request");
		HttpServletResponse response = (HttpServletResponse) data.get("response");

		String path = request.getParameter("path");
		String type = request.getParameter("type");
		String name = null;
		File file = null;
		boolean isTemp = false;
		if (StringUtil.isEmpty(type) || type.equalsIgnoreCase("FILE")) {
			file = param.getFile(path);
			name = file.getName();
		} else if (type.equalsIgnoreCase("REPOSITORY")) {
			isTemp = true;
			File tempFolder = new File(IDEConstant.WORKSPACES_TEMP_FOLDER);
			name = param.getSpaceName();
			file = new File(tempFolder, IDGenerateUtil.generateShort() + "/" + name);
			FileUtils.copyDirectory(param.getSourceFolder(), file, new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.isDirectory()) {
						if (pathname.getName().equals(".git")) {
							return false;
						} else if (pathname.getName().equals("node_modules")) {
							return false;
						}
					}
					return true;
				}
			});

		}

		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				name = name + ".zip";
			}
			response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, "UTF-8"));
			response.setCharacterEncoding("UTF-8");

			try {
				if (file.isFile()) {
					response.getOutputStream().write(FileUtil.read(file));
				} else if (file.isDirectory()) {
					ZipUtil.toZip(file, response.getOutputStream(), true);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {
					response.getOutputStream().close();
				} catch (Exception e) {
				}
				if (isTemp) {
					try {
						if (file.isFile()) {
							FileUtils.forceDelete(file);
						} else {
							FileUtils.deleteDirectory(file);
						}
					} catch (Exception e) {
					}
				}
			}
		}

	}

	public void upload(JSONObject data) throws Exception {
		HttpServletRequest request = (HttpServletRequest) data.get("request");
		HttpServletResponse response = (HttpServletResponse) data.get("response");

		// 构造一个文件上传处理对象
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		String tempPath = IDEConstant.WORKSPACES_TEMP_FOLDER + "upload/temp/";
		File fp1 = new File(tempPath);
		if (!fp1.exists())
			fp1.mkdirs();
		factory.setRepository(new File(tempPath));
		factory.setSizeThreshold(10240);

		List<FileItem> fileItems = new ArrayList<FileItem>();
		String fullPath = null;
		String repeat = null;
		String parent = null;
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			Iterator<?> items = upload.parseRequest(request).iterator();

			while (items.hasNext()) {
				FileItem item = (FileItem) items.next();
				if (!item.isFormField()) {
					fileItems.add(item);
				} else {
					if ("fullPath".equals(item.getFieldName())) {
						fullPath = item.getString();
					} else if ("repeat".equals(item.getFieldName())) {
						repeat = item.getString();
					} else if ("parent".equals(item.getFieldName())) {
						parent = item.getString();
					}
				}
			}

		}
		if (StringUtil.isNotEmpty(fullPath) && !"undefined".equals(fullPath) && fileItems.size() == 1) {

			FileItem fileItem = fileItems.get(0);

			File file = param.getFile(fullPath);
			if (StringUtil.isNotEmpty(parent)) {
				file = param.getFile(parent + "/" + fullPath);
			}
			if (file.exists()) {
				if (StringUtil.isEmpty(repeat) || repeat.equals("IGNORE")) {

				} else {
					FileUtil.write(fileItem.get(), file);
				}
			} else {
				FileUtil.write(fileItem.get(), file);
			}

		}

		Status status = Status.SUCCESS();

		ResponseUtil.outJSON(response, status);

	}
}
