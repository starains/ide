package com.teamide.ide.protect.processor.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class RepositoryFile extends RepositoryBase {

	public RepositoryFile(RepositoryProcessorParam param) {

		super(param);
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

		this.param.getLog().info("file paste,  path:" + path + ",  source:" + source);
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

		this.param.getLog().info("file save,  path:" + path);

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

		this.param.getLog().info("file rename,  path:" + path + ",  name:" + name);
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
		if (oldFile.getParent().equals(newFile.getParent())) {
			oldFile.renameTo(newFile);
		} else {
			FileUtil.write(FileUtil.read(oldFile), newFile);
			FileUtils.forceDelete(oldFile);
		}
		return callChange(folder);
	}

	public File create(final String parentPath, String name, boolean isFile, String content) throws Exception {

		this.param.getLog().info("file create,  parentPath:" + parentPath + ",  name:" + name);
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

		this.param.getLog().info("file delete,  path:" + path);

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

		this.param.getLog().info("file move,  path:" + path + ",  to:" + to);

		File file = this.param.getFile(path);
		File toFolder = this.param.getFile(to);
		if (!this.param.sourceContains(file)) {
			throw new Exception(path + " is not in the source directory.");
		}
		if (!this.param.sourceContains(toFolder)) {
			throw new Exception(to + " is not in the source directory.");
		}
		File folder = toFolder;
		if (file.exists()) {
			if (!toFolder.exists()) {
				toFolder.mkdirs();
			}
			if (file.isFile()) {
				FileUtils.moveFileToDirectory(file, toFolder, false);
			} else {
				FileUtils.moveDirectoryToDirectory(file, toFolder, false);
			}
			folder = callChange(toFolder);
		}
		return folder;
	}

	public File download(final String path) throws Exception {

		File file = this.param.getFile(path);

		return file;
	}
}
