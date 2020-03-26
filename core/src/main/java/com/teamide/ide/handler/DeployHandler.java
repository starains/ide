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
import com.teamide.ide.deployer.Deploy;
import com.teamide.ide.deployer.LocalDeploy;
import com.teamide.ide.deployer.RemoteDeploy;
import com.teamide.ide.enums.OptionType;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.starter.StarterServer;

public class DeployHandler {

	static final Map<String, Deploy> CACHE = new HashMap<String, Deploy>();

	static final Object LOAD_LOCK = new Object();

	static {
		File starterRootFolder = new File(IDEConstant.WORKSPACES_STARTER_FOLDER);

		StarterServer starterServer = new StarterServer(starterRootFolder);
		new Thread(starterServer).start();
	}

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

	public static Deploy get(String token) throws Exception {
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
		if (!new File(starterFolder, "starter.json").exists()) {
			return;
		}
		Deploy deploy = new LocalDeploy(starterFolder);
		if (deploy.option != null && StringUtil.isNotEmpty(deploy.option.getRemoteid())) {
			deploy = new RemoteDeploy(deploy.option.getRemoteid(), starterFolder);
		}
		CACHE.put(deploy.starter.token, deploy);
	}

	public static final String deploy(RepositoryProcessorParam param, String projectPath, String runName)
			throws Exception {
		JSONObject option = param.getOption(projectPath, runName, OptionType.STARTER);
		if (option == null) {
			throw new Exception("path [" + projectPath + "] run [" + runName + "] option is null.");
		}
		Deploy starter = create(param, projectPath, option);
		deploy(starter.starter.token);
		return starter.starter.token;
	}

	public static final void deploy(String token) throws Exception {
		Deploy starter = get(token);
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

	public static Deploy create(RepositoryProcessorParam param, String path, JSONObject option) throws Exception {
		String token = IDGenerateUtil.generateShort();
		JSONObject json = new JSONObject();
		json.put("spaceid", param.getSpaceid());
		json.put("branch", param.getBranch());
		json.put("token", token);
		json.put("path", path);
		json.put("option", option);
		json.put("timestamp", System.currentTimeMillis());
		if (option != null) {
			json.put("name", option.get("name"));
		}

		File starterFolder = getStarterFolder(token);

		FileUtil.write(json.toJSONString().getBytes(), new File(starterFolder, "starter.json"));

		return get(token);
	}

	public static List<Deploy> getStarters(RepositoryProcessorParam param) throws Exception {

		List<Deploy> starters = new ArrayList<Deploy>();
		for (String token : CACHE.keySet()) {
			Deploy starter = get(token);
			if (param.getSpaceid().equals(starter.spaceid) && param.getBranch().equals(starter.branch)) {
				starters.add(starter);
			}
		}

		return starters;
	}

	public static File getWorkspacesStarterFolder() {
		return new File(IDEConstant.WORKSPACES_STARTER_FOLDER);
	}

	public static File getStarterFolder(String token) {
		return new File(getWorkspacesStarterFolder(), token);
	}

}
