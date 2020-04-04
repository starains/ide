package com.teamide.ide.handler;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.param.SpaceFormatParam;

public class UserHandler {

	public static JSONObject getFormat(UserBean user, ClientSession session) throws Exception {
		if (user == null) {
			return null;
		}
		JSONObject USER = (JSONObject) JSONObject.toJSON(user);
		USER.remove("password");

		SpaceFormatParam spaceFormat = null;
		if (StringUtil.isNotEmpty(user.getSpaceid())) {
			spaceFormat = SpaceHandler.getFormat(user.getSpaceid(), session);
		}
		USER.put("space", spaceFormat);

		return USER;
	}
}
