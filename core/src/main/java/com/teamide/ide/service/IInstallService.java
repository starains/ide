package com.teamide.ide.service;

import com.alibaba.fastjson.JSONObject;

public interface IInstallService {

	public boolean installed();

	public boolean install(JSONObject json) throws Exception;
}
