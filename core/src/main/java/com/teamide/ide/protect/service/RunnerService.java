package com.teamide.ide.protect.service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.service.IRunnerService;

@Resource
public class RunnerService implements IRunnerService {

	@Override
	public JSONObject listen(JSONObject json) throws Exception {
		System.out.println(json);
		return new JSONObject();
	}

	@Override
	public JSONObject task(JSONObject json) throws Exception {
		return new JSONObject();
	}

	@Override
	public JSONObject notice(JSONObject json) throws Exception {
		return new JSONObject();
	}

	@Override
	public void download(JSONObject json, HttpServletResponse response) throws Exception {

	}

}
