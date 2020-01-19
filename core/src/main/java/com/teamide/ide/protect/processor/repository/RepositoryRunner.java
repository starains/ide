package com.teamide.ide.protect.processor.repository;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.runner.Runner;
import com.teamide.ide.protect.processor.repository.runner.RunnerHandler;

public class RepositoryRunner extends RepositoryBase {

	public RepositoryRunner(RepositoryProcessorParam param) {

		super(param);
	}

	public JSONObject deploy(String token) throws Exception {

		RunnerHandler.deploy(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject deploy(String path, JSONObject option) throws Exception {

		String token = RunnerHandler.deploy(this.param, path, option.getString("name"));
		JSONObject result = new JSONObject();
		result.put("token", token);
		return result;
	}

	public JSONObject start(String token) throws Exception {

		RunnerHandler.start(this.param, token);
		JSONObject result = new JSONObject();
		result.put("token", token);
		return result;
	}

	public JSONObject stop(String token) throws Exception {

		RunnerHandler.stop(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject remove(String token) throws Exception {

		RunnerHandler.remove(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject logClean(String token) throws Exception {
		JSONObject result = new JSONObject();
		RepositoryLog log = this.param.getRunnerLog(token);
		log.clean();
		return result;
	}

	public JSONObject status(String token) throws Exception {

		JSONObject result = RunnerHandler.status(this.param, token);
		return result;
	}

	public JSONArray loadRunners() throws Exception {
		JSONArray res = new JSONArray();
		List<Runner> runners = RunnerHandler.getRunners(this.param);
		for (Runner runner : runners) {
			res.add(runner.getRunnerInfo());
		}
		return res;
	}

}
