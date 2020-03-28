package com.teamide.ide.plugin;

import com.alibaba.fastjson.JSONObject;

public interface IDERepositoryListener extends IDEListener {

	public void onLoad(JSONObject data, String path);

}
