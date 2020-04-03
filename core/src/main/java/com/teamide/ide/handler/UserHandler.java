package com.teamide.ide.handler;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.UserBean;

public class UserHandler {

	public static JSONObject getFormat(UserBean user) throws Exception {
		if (user == null) {
			return null;
		}
		JSONObject USER = (JSONObject) JSONObject.toJSON(user);
		USER.remove("password");

		JSONObject space = null;
		if (StringUtil.isNotEmpty(user.getSpaceid())) {
			space = SpaceHandler.getFormat(user.getSpaceid());
		}
		USER.put("space", space);

		return USER;
	}
}
