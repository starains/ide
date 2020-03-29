package com.teamide.ide.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.param.ProjectParam;
import com.teamide.ide.processor.param.ProjectOption;
import com.teamide.ide.processor.repository.project.FileBean;
import com.teamide.ide.processor.repository.project.ProjectBean;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class PluginHandler {

	final static Map<String, PluginLoader> CACHE = new HashMap<String, PluginLoader>();

	final static Object LOCK = new Object();

	public static List<File> getJarFiles() {
		List<File> files = FileUtil.loadAllFiles(IDEConstant.PLUGINS_FOLDER);
		List<File> jarFiles = new ArrayList<File>();
		for (File jarFile : files) {
			if (!jarFile.isFile() || !jarFile.getName().endsWith(".jar")) {
				continue;
			}
			jarFiles.add(jarFile);
		}
		return jarFiles;
	}

	public static void load() {
		List<File> jarFiles = getJarFiles();
		for (File jarFile : jarFiles) {
			PluginLoader loader = new PluginLoader(jarFile);
			IDEPlugin plugin = loader.getPlugin();
			if (plugin != null) {
				String key = getKey(plugin);
				CACHE.put(key, loader);
			}
		}
	}

	private static void init() {
		if (CACHE.size() == 0) {
			synchronized (LOCK) {
				if (CACHE.size() == 0) {
					load();
				}
			}
		}
		synchronized (LOCK) {
			for (PluginLoader loader : CACHE.values()) {
				if (loader.changed()) {
					loader.reload();
				}
			}
		}
	}

	public static String getKey(IDEPlugin plugin) {
		return getKey(plugin.getName(), plugin.getVersion());
	}

	public static String getKey(String name, String version) {
		return name + "-" + version;
	}

	public static PluginLoader getPluginLoader(String name, String version) {
		init();
		String key = getKey(name, version);
		return CACHE.get(key);
	}

	public static IDEPlugin getPlugin(String name, String version) {
		PluginLoader loader = getPluginLoader(name, version);

		if (loader != null) {
			return loader.getPlugin();
		}
		return null;
	}

	public static List<IDEPlugin> getPlugins() {
		init();
		List<IDEPlugin> plugins = new ArrayList<IDEPlugin>();
		for (PluginLoader loader : CACHE.values()) {
			if (loader == null || loader.getPlugin() == null) {
				continue;
			}
			plugins.add(loader.getPlugin());
		}

		return plugins;
	}

	public static JSONObject getEnumMap() {

		JSONObject ENUM_MAP = new JSONObject();

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null) {
				continue;
			}
			JSONObject json = plugin.getEnumMap();
			if (json != null) {
				ENUM_MAP.putAll(json);
			}
		}
		return ENUM_MAP;
	}

	public static List<IDEResource> getResources() {

		List<IDEResource> resources = new ArrayList<IDEResource>();

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null) {
				continue;
			}
			List<IDEResource> list = plugin.getResources();
			if (list != null) {
				resources.addAll(list);
			}
		}
		return resources;
	}

	public static List<IDEContextmenu> getContextmenus() {

		List<IDEContextmenu> contextmenus = new ArrayList<IDEContextmenu>();

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null) {
				continue;
			}
			List<IDEContextmenu> list = plugin.getContextmenus();
			if (list != null) {
				contextmenus.addAll(list);
			}
		}
		return contextmenus;
	}

	public static PluginParam getParam(ProjectParam param, IDEPlugin plugin) {
		ProjectOption projectOption = new ProjectOption(param);
		JSONObject option = new JSONObject();
		if (StringUtil.isNotEmpty(plugin.getOptionType())) {
			try {
				option = projectOption.getOptionByType(plugin.getOptionType());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new PluginParam(param, option);

	}

	public static void loadProject(ProjectParam param, ProjectBean project) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();

			PluginParam pluginParam = getParam(param, plugin);
			Object value = listener.onLoad(pluginParam, project.getPath());

			project.setAttribute(plugin.getProjectAttributeName(), value);
		}
	}

	public static void loadFile(ProjectParam param, FileBean fileBean, File file) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			JSONObject data = new JSONObject();

			PluginParam pluginParam = getParam(param, plugin);

			listener.onLoadFile(pluginParam, file);

			fileBean.setAttribute(plugin.getFileAttributeName(), data);
		}
	}

	public static void createFile(ProjectParam param, File file) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();

			PluginParam pluginParam = getParam(param, plugin);
			listener.onCreateFile(pluginParam, file);
		}
	}

	public static void createFolder(ProjectParam param, File folder) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			PluginParam pluginParam = getParam(param, plugin);
			listener.onCreateFile(pluginParam, folder);
		}
	}

	public static void updateFile(ProjectParam param, File file) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			PluginParam pluginParam = getParam(param, plugin);
			listener.onUpdateFile(pluginParam, file);
		}
	}

	public static void deleteFile(ProjectParam param, File file) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			PluginParam pluginParam = getParam(param, plugin);
			listener.onDeleteFile(pluginParam, file);
		}
	}

	public static void deleteFolder(ProjectParam param, File folder) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			PluginParam pluginParam = getParam(param, plugin);
			listener.onDeleteFolder(pluginParam, folder);
		}
	}

	public static void renameFile(ProjectParam param, File oldFile, File newFile) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			PluginParam pluginParam = getParam(param, plugin);
			listener.onRenameFile(pluginParam, oldFile, newFile);
		}
	}

	public static void renameFolder(ProjectParam param, File oldFolder, File newFolder) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			PluginParam pluginParam = getParam(param, plugin);
			listener.onRenameFolder(pluginParam, oldFolder, newFolder);
		}
	}

	public static void moveFile(ProjectParam param, File oldFile, File newFile) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			PluginParam pluginParam = getParam(param, plugin);
			listener.onMoveFile(pluginParam, oldFile, newFile);
		}
	}

	public static void moveFolder(ProjectParam param, File oldFolder, File newFolder) {

		List<IDEPlugin> plugins = getPlugins();

		for (IDEPlugin plugin : plugins) {
			if (plugin == null || plugin.getProjectListener() == null) {
				continue;
			}
			IDERepositoryProjectListener listener = plugin.getProjectListener();
			PluginParam pluginParam = getParam(param, plugin);
			listener.onMoveFolder(pluginParam, oldFolder, newFolder);
		}
	}
}
