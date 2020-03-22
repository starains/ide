package com.teamide.ide.processor.repository;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.handler.StarterHandler;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.starter.Starter;

public class RepositoryStarter extends RepositoryBase {

	public RepositoryStarter(RepositoryProcessorParam param) {

		super(param);
	}

	public JSONObject deploy(String path, JSONObject option) throws Exception {

		this.param.getLog().info("starter deploy,  path:" + path);

		String token = StarterHandler.deploy(this.param, path, option.getString("name"));
		JSONObject result = new JSONObject();
		result.put("token", token);
		return result;
	}

	public JSONObject deploy(String token) throws Exception {

		this.param.getLog().info("starter deploy,  token:" + token);

		StarterHandler.deploy(token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject stop(String token) throws Exception {

		this.param.getLog().info("starter stop,  token:" + token);
		Starter starter = StarterHandler.get(token);

		if (starter != null) {
			starter.stop();
		}
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject start(String token) throws Exception {

		this.param.getLog().info("starter start,  token:" + token);

		Starter starter = StarterHandler.get(token);

		if (starter != null) {
			starter.start();
		}
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject destroy(String token) throws Exception {

		this.param.getLog().info("starter destroy,  token:" + token);

		Starter starter = StarterHandler.get(token);

		if (starter != null) {
			starter.destroy();
		}
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject remove(String token) throws Exception {

		this.param.getLog().info("starter remove,  token:" + token);

		StarterHandler.remove(token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject logClean(String token) throws Exception {

		this.param.getLog().info("starter log clean,  token:" + token);
		Starter starter = StarterHandler.get(token);

		if (starter != null) {
			starter.getStarterLog().clean();

		}
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject status(String token) throws Exception {
		Starter starter = StarterHandler.get(token);

		if (starter != null) {
			return starter.getStarterInfo();
		}
		return null;
	}

	public JSONArray loadStarters() throws Exception {
		JSONArray res = new JSONArray();
		List<Starter> starters = StarterHandler.getStarters(this.param);
		for (Starter starter : starters) {
			res.add(starter.getStarterInfo());
		}
		return res;
	}

}
