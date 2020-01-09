package com.teamide.protect.ide.processor.repository.starter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.FileUtil;
import com.teamide.util.IDGenerateUtil;
import com.teamide.client.ClientSession;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.protect.ide.enums.OptionType;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.RepositoryLog;

public class StarterHandler {

	public static final String start(RepositoryProcessorParam param, String projectPath, String runName)
			throws Exception {
		Starter starter = createStarter(param, projectPath, runName);
		new Thread() {

			@Override
			public void run() {
				try {
					starter.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
		return starter.token;
	}

	public static final void start(RepositoryProcessorParam param, String token) throws Exception {
		Starter starter = getStarter(param, token);
		if (starter != null) {
			new Thread() {
				@Override
				public void run() {
					try {
						starter.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.start();
		}
	}

	public static final void stop(RepositoryProcessorParam param, String token) throws Exception {
		Starter starter = getStarter(param, token);
		if (starter != null) {
			starter.stop();
		}
	}

	public static final JSONObject status(RepositoryProcessorParam param, String token) throws Exception {
		Starter starter = getStarter(param, token);
		if (starter != null) {
			return starter.getStarterInfo();
		}
		return null;
	}

	public static final void destroy(RepositoryProcessorParam param, String token) throws Exception {
		Starter starter = getStarter(param, token);
		if (starter != null) {
			starter.destroy();
		}
	}

	public static final void remove(RepositoryProcessorParam param, String token) throws Exception {
		Starter starter = getStarter(param, token);
		if (starter != null) {
			starter.remove();
		}
	}

	public static Starter getStarter(RepositoryProcessorParam param, String token) throws Exception {
		Starter starter = getStarter(param.getSession(), token);
		return starter;
	}

	public static Starter createStarter(RepositoryProcessorParam param, String projectPath, String runName)
			throws Exception {

		JSONObject option = param.getOption(projectPath, runName, OptionType.STARTER);
		if (option == null) {
			throw new Exception("path [" + projectPath + "] run [" + runName + "] option is null.");
		}
		Starter starter = createStarter(param, projectPath, option);

		return starter;
	}

	public static List<Starter> getStarters(RepositoryProcessorParam param) throws Exception {

		File starterFolder = getWorkspacesStarterFolder();

		List<Starter> starters = new ArrayList<Starter>();
		if (starterFolder.exists() && starterFolder.isDirectory()) {
			File[] files = starterFolder.listFiles();
			for (File f : files) {
				Starter starter = getStarter(param, f);
				if (starter != null) {
					if (param.getSpaceid().equals(starter.param.getSpaceid())
							&& param.getBranch().equals(starter.param.getBranch())) {
						starters.add(starter);
					}
				}
			}
		}

		return starters;
	}

	public static Starter getStarter(RepositoryProcessorParam param, File root) {
		if (root == null || !root.exists()) {
			return null;
		}
		Starter starter = null;
		File starterJSONFile = getStarterJSONFileByFolder(root);
		if (!starterJSONFile.exists() || !starterJSONFile.isFile()) {
		} else {
			try {
				String content = new String(FileUtil.read(starterJSONFile));
				JSONObject json = JSONObject.parseObject(content);
				String token = json.getString("token");
				starter = getStarter(param.getSession(), token);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (starter == null) {
			try {
				FileUtils.deleteDirectory(root);
			} catch (Exception e) {
			}
		}
		return starter;

	}

	public static Starter createStarter(RepositoryProcessorParam param, String path, JSONObject option)
			throws Exception {
		String token = IDGenerateUtil.generateShort();
		JSONObject json = new JSONObject();
		json.put("spaceid", param.getSpaceid());
		json.put("branch", param.getBranch());
		json.put("token", token);
		json.put("path", path);
		json.put("option", option);
		json.put("timestamp", System.currentTimeMillis());

		FileUtil.write(json.toJSONString().getBytes(), getStarterJSONFile(token));

		return getStarter(param.getSession(), token);
	}

	public static Starter getStarter(ClientSession session, String token) {
		Starter starter = new Starter(session, token);
		if (starter.param == null) {
			return null;
		}
		return starter;
	}

	public static File getWorkspacesStarterFolder() {
		return new File(IDEConstant.WORKSPACES_STARTER_FOLDER);
	}

	public static File getStarterFolder(String token) {
		return new File(getWorkspacesStarterFolder(), token);
	}

	public static File getStarterLogFolder(String token) {
		return new File(getStarterFolder(token), "log");
	}

	public static File getStarterJSONFile(String token) {
		return getStarterJSONFileByFolder(getStarterFolder(token));
	}

	public static File getStarterJSONFileByFolder(File folder) {
		return new File(folder, "starter.json");
	}

	public static RepositoryLog getStarterLog(String token) {

		return RepositoryLog.get("starter", getStarterLogFolder(token));
	}

	public static JSONObject readJSON(String token) {
		File jsonFile = getStarterJSONFile(token);
		if (jsonFile.exists()) {
			try {
				String content = new String(FileUtil.read(jsonFile));
				return JSONObject.parseObject(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
