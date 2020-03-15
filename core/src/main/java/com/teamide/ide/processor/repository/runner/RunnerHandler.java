package com.teamide.ide.processor.repository.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.util.FileUtil;

public class RunnerHandler {

	public static final Map<String, Runner> CACHE = new HashMap<String, Runner>();

	public static final String deploy(RepositoryProcessorParam param, String path, String name) throws Exception {
		Runner runner = createRunner(param, path, name);
		runner.deploy();
		return runner.token;
	}

	public static final void deploy(RepositoryProcessorParam param, String token) throws Exception {
		Runner runner = getRunner(param, token);
		if (runner != null) {
			runner.deploy();
		}
	}

	public static final void start(RepositoryProcessorParam param, String token) throws Exception {
		Runner runner = getRunner(param, token);
		if (runner != null) {
			runner.startup();
		}
	}

	public static final void stop(RepositoryProcessorParam param, String token) throws Exception {
		Runner runner = getRunner(param, token);
		if (runner != null) {
			runner.shutdown();
		}
	}

	public static final JSONObject status(RepositoryProcessorParam param, String token) throws Exception {
		Runner runner = getRunner(param, token);
		if (runner != null) {
			return runner.getRunnerInfo();
		}
		return null;
	}

	public static final void remove(RepositoryProcessorParam param, String token) throws Exception {
		Runner runner = getRunner(param, token);
		if (runner != null) {
			runner.destroy();
		}
	}

	public static Runner getRunner(RepositoryProcessorParam param, String token) throws Exception {
		Runner runner = CACHE.get(token);
		if (runner == null) {
			runner = getRunner(param, param.getTokenRunnerFolder(token));
		}
		if (runner != null) {
			CACHE.put(token, runner);
		}
		return runner;
	}

	public static Runner createRunner(RepositoryProcessorParam param, String projectPath, String runName)
			throws Exception {

		Runner runner = new Runner(param, projectPath, runName);

		CACHE.put(runner.token, runner);
		return runner;
	}

	public static Runner createRunner(RepositoryProcessorParam param, JSONObject json) throws Exception {

		Runner runner = new Runner(param, json);
		CACHE.put(runner.token, runner);
		return runner;
	}

	public static List<Runner> getRunners(RepositoryProcessorParam param) throws Exception {

		File runnerFolder = param.getRunnerFolder();

		List<Runner> runners = new ArrayList<Runner>();
		if (runnerFolder.exists() && runnerFolder.isDirectory()) {
			File[] files = runnerFolder.listFiles();
			for (File f : files) {
				Runner runner = getRunner(param, f);
				if (runner != null) {
					runners.add(runner);
				}
			}
		}

		return runners;
	}

	public static Runner getRunner(RepositoryProcessorParam param, File root) {
		if (root == null || !root.exists()) {
			return null;
		}
		boolean needDelete = false;
		File runnerJSONFile = new File(root, "runner.json");
		if (!runnerJSONFile.exists() || !runnerJSONFile.isFile()) {
			needDelete = true;
		} else {

			try {
				String content = new String(FileUtil.read(runnerJSONFile));
				JSONObject json = JSONObject.parseObject(content);
				String token = json.getString("token");
				Runner runner = CACHE.get(token);
				if (runner == null) {
					runner = createRunner(param, json);
				}

				return runner;
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
