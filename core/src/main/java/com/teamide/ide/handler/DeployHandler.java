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
import com.teamide.ide.param.ProjectProcessorParam;
import com.teamide.ide.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.RepositoryProject;
import com.teamide.starter.StarterServer;
import com.teamide.starter.bean.JavaOptionBean;

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
		if (deploy != null && deploy.starter.option instanceof JavaOptionBean) {
			JavaOptionBean javaOption = (JavaOptionBean) deploy.starter.option;
			if (StringUtil.isNotEmpty(javaOption.getRemoteid())) {
				deploy = new RemoteDeploy(javaOption.getRemoteid(), starterFolder);
			}
		}
		CACHE.put(deploy.starter.token, deploy);
	}

	public static final String deploy(RepositoryProcessorParam param, String projectPath, String runName)
			throws Exception {
		ProjectProcessorParam projectParam = new ProjectProcessorParam(param, projectPath);
		JSONObject option = new RepositoryProject(projectParam).readStarter(projectPath, runName);
		if (option == null) {
			throw new Exception("path [" + projectPath + "] run [" + runName + "] option is null.");
		}
		Deploy starter = create(projectParam, projectPath, option);
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

	public static Deploy create(ProjectProcessorParam param, String path, JSONObject option) throws Exception {
		String token = IDGenerateUtil.generateShort();
		option = option == null ? new JSONObject() : option;
		option.put("spaceid", param.getSpaceid());
		option.put("branch", param.getBranch());
		option.put("token", token);
		option.put("path", param.getFile(path).toURI().getPath());
		option.put("timestamp", System.currentTimeMillis());

		File starterFolder = getStarterFolder(token);

		FileUtil.write(option.toJSONString().getBytes(), new File(starterFolder, "starter.json"));

		return get(token);
	}

	public static List<Deploy> getStarters(RepositoryProcessorParam param) throws Exception {

		List<Deploy> deploys = new ArrayList<Deploy>();
		for (String token : CACHE.keySet()) {
			Deploy deploy = get(token);
			if (deploy != null) {
				if (param.getSpaceid().equals(deploy.starter.option.getSpaceid())
						&& param.getBranch().equals(deploy.starter.option.getBranch())) {
					deploys.add(deploy);
				}
			}
		}

		return deploys;
	}

	public static File getWorkspacesStarterFolder() {
		return new File(IDEConstant.WORKSPACES_STARTER_FOLDER);
	}

	public static File getStarterFolder(String token) {
		return new File(getWorkspacesStarterFolder(), token);
	}

}
