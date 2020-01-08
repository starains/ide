package com.teamide.ide.service;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

public interface IRunnerService {

	public JSONObject listen(JSONObject json) throws Exception;

	public JSONObject task(JSONObject json) throws Exception;

	public JSONObject notice(JSONObject json) throws Exception;

	public void download(JSONObject json, HttpServletResponse response) throws Exception;

}
