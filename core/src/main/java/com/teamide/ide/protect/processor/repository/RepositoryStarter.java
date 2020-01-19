package com.teamide.ide.protect.processor.repository;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.starter.Starter;
import com.teamide.ide.protect.processor.repository.starter.StarterHandler;

public class RepositoryStarter extends RepositoryBase {

	public RepositoryStarter(RepositoryProcessorParam param) {

		super(param);
	}

	public JSONObject start(String path, JSONObject option) throws Exception {

		this.param.getLog().info("starter start,  path:" + path);

		String token = StarterHandler.start(this.param, path, option.getString("name"));
		JSONObject result = new JSONObject();
		result.put("token", token);
		return result;
	}

	public JSONObject stop(String token) throws Exception {

		this.param.getLog().info("starter stop,  token:" + token);

		StarterHandler.stop(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject start(String token) throws Exception {

		this.param.getLog().info("starter start,  token:" + token);

		StarterHandler.start(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject destroy(String token) throws Exception {

		this.param.getLog().info("starter destroy,  token:" + token);

		StarterHandler.destroy(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject remove(String token) throws Exception {

		this.param.getLog().info("starter remove,  token:" + token);

		StarterHandler.remove(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject logClean(String token) throws Exception {

		this.param.getLog().info("starter log clean,  token:" + token);
		JSONObject result = new JSONObject();
		StarterHandler.getStarterLog(token).clean();
		return result;
	}

	public JSONObject status(String token) throws Exception {

		JSONObject result = StarterHandler.status(this.param, token);
		return result;
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
