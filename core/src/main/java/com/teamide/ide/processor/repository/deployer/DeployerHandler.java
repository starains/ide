package com.teamide.ide.processor.repository.deployer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.util.FileUtil;

public class DeployerHandler {

	public static final Map<String, Deployer> CACHE = new HashMap<String, Deployer>();

	public static final String deploy(RepositoryProcessorParam param, String path, String name) throws Exception {
		Deployer deployer = createDeployer(param, path, name);
		deployer.deploy();
		return deployer.token;
	}

	public static final void deploy(RepositoryProcessorParam param, String token) throws Exception {
		Deployer deployer = getDeployer(param, token);
		if (deployer != null) {
			deployer.deploy();
		}
	}

	public static final void start(RepositoryProcessorParam param, String token) throws Exception {
		Deployer deployer = getDeployer(param, token);
		if (deployer != null) {
			deployer.startup();
		}
	}

	public static final void stop(RepositoryProcessorParam param, String token) throws Exception {
		Deployer deployer = getDeployer(param, token);
		if (deployer != null) {
			deployer.shutdown();
		}
	}

	public static final JSONObject status(RepositoryProcessorParam param, String token) throws Exception {
		Deployer deployer = getDeployer(param, token);
		if (deployer != null) {
			return deployer.getDeployerInfo();
		}
		return null;
	}

	public static final void remove(RepositoryProcessorParam param, String token) throws Exception {
		Deployer deployer = getDeployer(param, token);
		if (deployer != null) {
			deployer.destroy();
		}
	}

	public static Deployer getDeployer(RepositoryProcessorParam param, String token) throws Exception {
		Deployer deployer = CACHE.get(token);
		if (deployer == null) {
			deployer = getDeployer(param, param.getTokenDeployerFolder(token));
		}
		if (deployer != null) {
			CACHE.put(token, deployer);
		}
		return deployer;
	}

	public static Deployer createDeployer(RepositoryProcessorParam param, String projectPath, String runName)
			throws Exception {

		Deployer deployer = new Deployer(param, projectPath, runName);

		CACHE.put(deployer.token, deployer);
		return deployer;
	}

	public static Deployer createDeployer(RepositoryProcessorParam param, JSONObject json) throws Exception {

		Deployer deployer = new Deployer(param, json);
		CACHE.put(deployer.token, deployer);
		return deployer;
	}

	public static List<Deployer> getDeployers(RepositoryProcessorParam param) throws Exception {

		File deployerFolder = param.getDeployerFolder();

		List<Deployer> deployers = new ArrayList<Deployer>();
		if (deployerFolder.exists() && deployerFolder.isDirectory()) {
			File[] files = deployerFolder.listFiles();
			for (File f : files) {
				Deployer deployer = getDeployer(param, f);
				if (deployer != null) {
					deployers.add(deployer);
				}
			}
		}

		return deployers;
	}

	public static Deployer getDeployer(RepositoryProcessorParam param, File root) {
		if (root == null || !root.exists()) {
			return null;
		}
		boolean needDelete = false;
		File deployerJSONFile = new File(root, "deployer.json");
		if (!deployerJSONFile.exists() || !deployerJSONFile.isFile()) {
			needDelete = true;
		} else {

			try {
				String content = new String(FileUtil.read(deployerJSONFile));
				JSONObject json = JSONObject.parseObject(content);
				String token = json.getString("token");
				Deployer deployer = CACHE.get(token);
				if (deployer == null) {
					deployer = createDeployer(param, json);
				}

				return deployer;
			} catch (Exception e) {
				e.printStackTrace();
				needDelete = true;
			}
		}

		if (needDelete) {
			try {
				FileUtils.deleteDirectory(root);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}
}
