package com.teamide.ide.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.processor.repository.project.FileBean;
import com.teamide.ide.processor.repository.project.ProjectBean;

public class PluginHandler {

	final static Map<String, IDEPlugin> CACHE = new HashMap<String, IDEPlugin>();

	final static Object LOCK = new Object();

	public static void load() {

	}

	private static void init() {
		if (CACHE.size() == 0) {
			synchronized (LOCK) {
				if (CACHE.size() == 0) {
					load();
				}
			}
		}
	}

	public static void loadProject(ProjectBean project, File folder) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();

			JSONObject data = new JSONObject();

			listener.onLoad(data, folder);

			project.setAttribute(plugin.getProjectAttributeName(), data);
		}
	}

	public static void loadFile(FileBean fileBean, File file) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			JSONObject data = new JSONObject();

			listener.onLoadFile(data, file);

			fileBean.setAttribute(plugin.getFileAttributeName(), data);
		}
	}

	public static void createFile(JSONObject data, File file) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onCreateFile(data, file);
		}
	}

	public static void createFolder(JSONObject data, File folder) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onCreateFolder(data, folder);
		}
	}

	public static void updateFile(JSONObject data, File file) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onUpdateFile(data, file);
		}
	}

	public static void deleteFile(JSONObject data, File file) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onDeleteFile(data, file);
		}
	}

	public static void deleteFolder(JSONObject data, File folder) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onDeleteFolder(data, folder);
		}
	}

	public static void renameFile(JSONObject data, File oldFile, File newFile) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onRenameFile(data, oldFile, newFile);
		}
	}

	public static void renameFolder(JSONObject data, File oldFolder, File newFolder) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onRenameFolder(data, oldFolder, newFolder);
		}
	}

	public static void moveFile(JSONObject data, File oldFile, File newFile) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onMoveFile(data, oldFile, newFile);
		}
	}

	public static void moveFolder(JSONObject data, File oldFolder, File newFolder) {

		init();

		for (IDEPlugin plugin : CACHE.values()) {
			if (plugin == null || plugin.getRepositoryProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getRepositoryProjectListener();
			listener.onMoveFolder(data, oldFolder, newFolder);
		}
	}
}
