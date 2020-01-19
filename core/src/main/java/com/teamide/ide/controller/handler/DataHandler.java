package com.teamide.ide.controller.handler;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.Application;
import com.teamide.app.ApplicationFactory;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.enums.BeanModelType;
import com.teamide.app.enums.ColumnType;
import com.teamide.app.enums.ComparisonOperator;
import com.teamide.app.enums.DaoProcessType;
import com.teamide.app.enums.ServiceProcessType;
import com.teamide.app.enums.SqlType;
import com.teamide.app.util.ModelFileUtil;
import com.teamide.bean.Status;
import com.teamide.client.ClientHandler;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.enums.EnvironmentType;
import com.teamide.ide.enums.PublicType;
import com.teamide.ide.enums.SpacePermission;
import com.teamide.ide.enums.SpaceTeamType;
import com.teamide.ide.enums.SpaceType;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.filter.IDEFilter;
import com.teamide.ide.protect.processor.enums.ModelType;
import com.teamide.ide.protect.processor.enums.RepositoryModelType;
import com.teamide.ide.protect.processor.enums.SpaceModelType;
import com.teamide.ide.protect.service.InstallService;
import com.teamide.ide.protect.service.SpaceService;
import com.teamide.ide.protect.util.TokenUtil;
import com.teamide.ide.service.IInstallService;
import com.teamide.ide.service.ISpaceService;
import com.teamide.param.DataParam;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.RequestUtil;
import com.teamide.util.ResponseUtil;
import com.teamide.util.StringUtil;
import com.teamide.variable.enums.ValueType;

public class DataHandler {

	public void handle(String path, HttpServletRequest request, HttpServletResponse response) {
		if (path.endsWith("/toText")) {
			toText(request, response);
		} else if (path.endsWith("/toModel")) {
			toModel(request, response);
		} else if (path.endsWith("/doTest")) {
			doTest(request, response);
		} else if (path.endsWith("/session")) {
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
		ENUM_MAP.put("DAO_PROCESS_TYPE", array);
		for (DaoProcessType type : DaoProcessType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("SQL_TYPE", array);
		for (SqlType type : SqlType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("COMPARISON_OPERATOR", array);
		for (ComparisonOperator type : ComparisonOperator.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("SERVICE_PROCESS_TYPE", array);
		for (ServiceProcessType type : ServiceProcessType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("COLUMN_TYPE", array);
		for (ColumnType type : ColumnType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
		}

		array = new JSONArray();
		ENUM_MAP.put("VALUE_TYPE", array);
		for (ValueType type : ValueType.values()) {
			JSONObject one = new JSONObject();
			array.add(one);
			one.put("text", type.getText());
			one.put("value", type.getValue());
			one.put("pattern", type.getPattern());
		}
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

	public void toText(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = JSONObject.parseObject(RequestUtil.getBody(request));
		JSONObject model = json.getJSONObject("model");
		String type = json.getString("type");
		String filename = json.getString("filename");
		BeanModelType modelType = BeanModelType.get(type);
		String text = "";
		if (modelType != null) {
			try {
				String fileType = ModelFileUtil.getTypeByFileName(filename);
				Object bean = ModelFileUtil.jsonToBean(modelType.getBeanClass(), model);
				text = ModelFileUtil.beanToText(bean, fileType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ResponseUtil.outText(response, text);
		return;
	}

	public void toModel(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = JSONObject.parseObject(RequestUtil.getBody(request));
		String text = json.getString("text");
		String type = json.getString("type");
		String filename = json.getString("filename");
		BeanModelType modelType = BeanModelType.get(type);
		Status status = Status.SUCCESS();
		if (modelType != null) {
			try {
				String fileType = ModelFileUtil.getTypeByFileName(filename);
				Object bean = ModelFileUtil.textToBean(modelType.getBeanClass(), text, fileType);
				status.setErrcode(0);
				status.setValue(bean);
			} catch (Exception e) {
				e.printStackTrace();
				status.setErrcode(-1);
				status.setErrmsg(e.getMessage());
			}
		}
		ResponseUtil.outJSON(response, status);
		return;
	}

	public void doTest(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = JSONObject.parseObject(RequestUtil.getBody(request));
		String data = json.getString("data");
		String type = json.getString("type");
		String path = json.getString("path");
		String name = json.getString("name");
		Object result = null;
		try {
			Application application = ApplicationFactory.start(new File(path));
			DataParam param = new DataParam((JSON) JSON.parse(data));

			if (type.equalsIgnoreCase("DAO")) {
				DaoBean dao = application.getContext().get(DaoBean.class, name);
				result = application.invokeDao(dao, param);
				application.responseResult(dao, result, response, true);
				return;
			} else if (type.equalsIgnoreCase("SERVICE")) {
				ServiceBean service = application.getContext().get(ServiceBean.class, name);
				result = application.invokeService(service, param);
				application.responseResult(service, result, response, true);
				return;
			}
		} catch (Exception e) {
			Status status = new Status();
			status.setErrcode(-1);
			status.setErrmsg(e.getMessage());
			result = status;
			e.printStackTrace();
		}

		ResponseUtil.outJSON(response, result);
		return;
	}
}
