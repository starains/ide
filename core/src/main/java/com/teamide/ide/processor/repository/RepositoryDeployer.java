package com.teamide.ide.processor.repository;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.deployer.Deployer;
import com.teamide.ide.processor.repository.deployer.DeployerHandler;

public class RepositoryDeployer extends RepositoryBase {

	public RepositoryDeployer(RepositoryProcessorParam param) {

		super(param);
	}

	public JSONObject deploy(String token) throws Exception {

		DeployerHandler.deploy(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject deploy(String path, JSONObject option) throws Exception {

		String token = DeployerHandler.deploy(this.param, path, option.getString("name"));
		JSONObject result = new JSONObject();
		result.put("token", token);
		return result;
	}

	public JSONObject start(String token) throws Exception {

		DeployerHandler.start(this.param, token);
		JSONObject result = new JSONObject();
		result.put("token", token);
		return result;
	}

	public JSONObject stop(String token) throws Exception {

		DeployerHandler.stop(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject remove(String token) throws Exception {

		DeployerHandler.remove(this.param, token);
		JSONObject result = new JSONObject();
		return result;
	}

	public JSONObject logClean(String token) throws Exception {
		JSONObject result = new JSONObject();
		RepositoryLog log = this.param.getDeployerLog(token);
		log.clean();
		return result;
	}

	public JSONObject status(String token) throws Exception {

		JSONObject result = DeployerHandler.status(this.param, token);
		return result;
	}

	public JSONArray loadDeployers() throws Exception {
		JSONArray res = new JSONArray();
		List<Deployer> deployers = DeployerHandler.getDeployers(this.param);
		for (Deployer deployer : deployers) {
			res.add(deployer.getDeployerInfo());
		}
		return res;
	}

}
