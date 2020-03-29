package com.teamide.ide.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.util.PropertyUtil;
import com.teamide.util.ResourceUtil;
import com.teamide.util.StringUtil;

public class PluginLoader {

	private final File jarFile;

	private long lastModified;

	private URLClassLoader classLoader;

	private IDEPlugin plugin;

	private final JSONObject info = new JSONObject();

	private final Object lock = new Object();

	public PluginLoader(File jarFile) {
		this.jarFile = jarFile;
		this.lastModified = jarFile.lastModified();
	}

	public boolean changed() {
		if (this.lastModified != jarFile.lastModified()) {
			return true;
		}
		return false;
	}

	private void load() {
		try {
			URL url = jarFile.toURI().toURL();
			this.classLoader = new URLClassLoader(new URL[] { url });

			InputStream stream = ResourceUtil.load(classLoader, "ide.plugin.properties");
			if (stream == null) {
				return;
			}
			Properties properties = PropertyUtil.get(stream);
			this.info.clear();
			JSONObject json = (JSONObject) JSON.toJSON(properties);
			this.info.putAll(json);
			String plugin = this.info.getString("plugin");
			if (StringUtil.isEmpty(plugin)) {
				return;
			}
			Class<?> clazz = this.classLoader.loadClass(plugin);
			if (clazz == null) {
				return;
			}
			this.plugin = (IDEPlugin) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.lastModified = jarFile.lastModified();
		}
	}

	public void close() {
		if (classLoader == null) {
			return;
		}

		try {
			this.lastModified = 0;
			this.info.clear();
			this.plugin = null;
			this.classLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		close();
		load();
	}

	private void init() {
		if (classLoader == null) {
			synchronized (lock) {
				if (classLoader == null) {
					load();
				}
			}
		}
	}

	public IDEPlugin getPlugin() {
		init();
		return plugin;
	}

	public InputStream load(String name) {
		init();
		return ResourceUtil.load(classLoader, name);
	}

	public File getJarFile() {
		return jarFile;
	}

	public long getLastModified() {
		return lastModified;
	}
}
