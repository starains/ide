package com.teamide.ide.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.Status;
import com.teamide.client.ClientHandler;
import com.teamide.http.Method;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.enums.EnvironmentType;
import com.teamide.ide.enums.PublicType;
import com.teamide.ide.enums.SpacePermission;
import com.teamide.ide.enums.SpaceTeamType;
import com.teamide.ide.enums.SpaceType;
import com.teamide.ide.enums.UserActiveStatus;
import com.teamide.ide.enums.UserStatus;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.filter.IDEFilter;
import com.teamide.ide.plugin.PluginHandler;
import com.teamide.ide.processor.enums.ModelType;
import com.teamide.ide.processor.enums.RepositoryModelType;
import com.teamide.ide.processor.enums.SpaceModelType;
import com.teamide.ide.service.IInstallService;
import com.teamide.ide.service.ISpaceService;
import com.teamide.ide.service.impl.InstallService;
import com.teamide.ide.service.impl.SpaceService;
import com.teamide.ide.util.TokenUtil;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.ResponseUtil;
import com.teamide.util.StringUtil;

public class DataHandler {

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) {
		if (path.endsWith("/session")) {
			session(request, response);
		}
	}

	public void session(HttpServletRequest request, HttpServletResponse response) {

		Status status = new Status();
		JSONObject data = new JSONObject();

		JSONObject json = new JSONObject();

		try {
			IInstallService installService = new InstallService();
			boolean installed = installService.installed();
			data.put("installed", installed);

			if (installed) {
				IDEConfigure configure = IDEConfigure.get();
				JSONObject CONFIGURE = (JSONObject) JSONObject.toJSON(configure);
				data.put("CONFIGURE", CONFIGURE);

				data.put("plugins", PluginHandler.getPlugins());
			}

			if (installed) {
				fullSpaces(request, data);
			}
			fullEnum(request, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.clear();
		json.put("sessionid", request.getSession().getId());
		if (StringUtil.isNotEmpty(data.getString("spaceid"))) {
			json.put("spaceid", data.getString("spaceid"));
		}
		if (StringUtil.isNotEmpty(data.getString("branch"))) {
			json.put("branch", data.getString("branch"));
		}
		json.put("timestamp", System.currentTimeMillis());
		json.put("token", IDGenerateUtil.generateShort());
		String token = TokenUtil.getToken(json);
		data.put("token", token);

		IDEFactory.setClientSession(token, ClientHandler.getSession(request));

		status.setValue(data);
		ResponseUtil.outJSON(response, status);

	}

	public static void fullEnum(HttpServletRequest request, JSONObject data) throws Exception {

		JSONObject ENUM_MAP = new JSONObject();
		data.put("ENUM_MAP", ENUM_MAP);

		JSONArray array = new JSONArray();

		array = new JSONArray();
		ENUM_MAP.put("MODEL_TYPE", array);
		for (ModelType type : ModelType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
			one.put("type", type.getType().getValue());
		}
		for (SpaceModelType type : SpaceModelType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
			one.put("type", type.getType().getValue());
		}
		for (RepositoryModelType type : RepositoryModelType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
			one.put("type", type.getType().getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("ENVIRONMENT_TYPE", array);
		for (EnvironmentType type : EnvironmentType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("PUBLIC_TYPE", array);
		for (PublicType type : PublicType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("SPACE_TYPE", array);
		for (SpaceType type : SpaceType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("SPACE_PERMISSION", array);
		for (SpacePermission type : SpacePermission.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("SPACE_TEAM_TYPE", array);
		for (SpaceTeamType type : SpaceTeamType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("HTTP_METHOD", array);
		for (Method method : Method.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", method.name());
			one.put("value", method.name());
		}

		array = new JSONArray();
		ENUM_MAP.put("USER_STATUS", array);
		for (UserStatus status : UserStatus.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", status.getText());
			one.put("value", status.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("USER_ACTIVE_STATUS", array);
		for (UserActiveStatus status : UserActiveStatus.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", status.getText());
			one.put("value", status.getValue());
		}

		ENUM_MAP.putAll(PluginHandler.getEnumMap());

	}

	public static void fullSpaces(HttpServletRequest request, JSONObject data) throws Exception {

		String SPACE_PATH = request.getParameter("SPACE_PATH");
		if (StringUtil.isEmpty(SPACE_PATH) || IDEFilter.ignore(SPACE_PATH)) {
			return;
		}
		SPACE_PATH = SPACE_PATH.replaceAll("//", "/");
		if (SPACE_PATH.startsWith("/")) {
			SPACE_PATH = SPACE_PATH.substring(1);
		}
		if (StringUtil.isEmpty(SPACE_PATH)) {
			return;
		}
		if (SPACE_PATH.indexOf("/") < 0) {
			SPACE_PATH = SPACE_PATH + "/";
		}
		String[] names = SPACE_PATH.split("/");

		ISpaceService spaceService = new SpaceService();
		boolean isFirst = true;
		String branch = null;
		SpaceType spaceType = null;
		SpaceBean last = null;
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (StringUtil.isEmpty(name)) {
				continue;
			}
			if (isFirst) {
				isFirst = false;
				for (SpaceType type : SpaceType.values()) {
					if (name.equalsIgnoreCase(type.getValue())) {
						spaceType = type;
						break;
					}
				}

				if (null != spaceType) {
					data.put("SPACE_TYPE", spaceType.getValue());
					continue;
				}
			}
			if (spaceType == null) {
				spaceType = SpaceType.USERS;
			}
			if (last != null) {
				last = spaceService.getByName(name, last.getId());
				if (last == null) {
					data.put("SPACE_NOT_FOUND", true);
					data.put("NOT_FOUND_SPACE", name);
					break;
				}
			} else {
				last = spaceService.getByName(name, spaceType);
				if (last == null) {
					data.put(spaceType.getValue() + "_NOT_FOUND", true);
					data.put("NOT_FOUND_SPACE", name);
					break;
				}
			}
			if (last != null) {
				if (spaceService.isRepositorys(last)) {
					i = i + 1;
					if (names.length > i) {
						branch = names[i];
					}
					if (StringUtil.isEmpty(branch)) {
						branch = "master";
					}
				}
			}

		}

		if (last != null) {
			data.put("spaceid", last.getId());
			data.put("branch", branch);
			data.put("SPACE_TYPE", last.getType());

		}

	}

}
