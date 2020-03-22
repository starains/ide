package com.teamide.ide.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.util.FileUtil;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.enums.OptionType;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.starter.Starter;

public class StarterHandler {

	static final Map<String, Starter> CACHE = new HashMap<String, Starter>();

	static final Object LOAD_LOCK = new Object();

	public static void reloadStarters() {
		CACHE.clear();
		loadStarters();

	}

	public static void loadStarters() {
		if (CACHE.size() > 0) {
			return;
		}
		synchronized (LOAD_LOCK) {
			if (CACHE.size() > 0) {
				return;
			}
			try {
				File folder = getWorkspacesStarterFolder();
				if (folder.exists()) {
					File[] files = folder.listFiles();
					for (File f : files) {
						if (f.isDirectory()) {
							loadStarter(f.getName());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void remove(String token) throws Exception {
		if (CACHE.get(token) != null) {
			CACHE.get(token).remove();
		}
		CACHE.remove(token);
	}

	public static Starter get(String token) throws Exception {
		if (StringUtil.isEmpty(token)) {
			return null;
		}

		loadStarters();

		if (CACHE.get(token) == null) {
			loadStarter(token);
		}
		if (CACHE.get(token) != null) {
			return CACHE.get(token);
		}
		return null;
	}

	public static void loadStarter(String token) throws Exception {
		if (CACHE.get(token) != null) {
			return;
		}
		synchronized (CACHE) {
			if (CACHE.get(token) != null) {
				return;
			}
		}

		File starterFolder = getStarterFolder(token);
		if (!new File(starterFolder, StarterHandler.JSON_FILE_NAME).exists()) {
			return;
		}
		Starter starter = new Starter(starterFolder);
		CACHE.put(starter.token, starter);
	}

	public static final String deploy(RepositoryProcessorParam param, String projectPath, String runName)
			throws Exception {
		JSONObject option = param.getOption(projectPath, runName, OptionType.STARTER);
		if (option == null) {
			throw new Exception("path [" + projectPath + "] run [" + runName + "] option is null.");
		}
		Starter starter = create(param, projectPath, option);
		deploy(starter.token);
		return starter.token;
	}

	public static final void deploy(String token) throws Exception {
		Starter starter = get(token);
		if (starter != null) {
			new Thread() {
				@Override
				public void run() {
					try {
						starter.deploy();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.start();
		}
	}

	public static Starter create(RepositoryProcessorParam param, String path, JSONObject option) throws Exception {
		String token = IDGenerateUtil.generateShort();
		JSONObject json = new JSONObject();
		json.put("spaceid", param.getSpaceid());
		json.put("branch", param.getBranch());
		json.put("token", token);
		json.put("path", path);
		json.put("option", option);
		json.put("timestamp", System.currentTimeMillis());

		File starterFolder = getStarterFolder(token);

		FileUtil.write(json.toJSONString().getBytes(), new File(starterFolder, JSON_FILE_NAME));

		return get(token);
	}

	public static List<Starter> getStarters(RepositoryProcessorParam param) throws Exception {

		List<Starter> starters = new ArrayList<Starter>();
		for (String token : CACHE.keySet()) {
			Starter starter = get(token);
			if (param.getSpaceid().equals(starter.spaceid) && param.getBranch().equals(starter.branch)) {
				starters.add(starter);
			}
		}

		return starters;
	}

	public static final String JSON_FILE_NAME = "starter.json";

	public static File getWorkspacesStarterFolder() {
		return new File(IDEConstant.WORKSPACES_STARTER_FOLDER);
	}

	public static File getStarterFolder(String token) {
		return new File(getWorkspacesStarterFolder(), token);
	}

}
