package com.teamide.app.plugin;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.enums.ColumnType;
import com.teamide.app.enums.ComparisonOperator;
import com.teamide.app.enums.DaoProcessType;
import com.teamide.app.enums.ServiceProcessType;
import com.teamide.app.enums.SqlType;
import com.teamide.app.variable.enums.ValueType;
import com.teamide.ide.plugin.IDEInput;
import com.teamide.ide.plugin.IDEListener;
import com.teamide.ide.plugin.IDEPlugin;
import com.teamide.ide.plugin.IDERepositoryListener;
import com.teamide.ide.plugin.IDERepositoryProjectListener;
import com.teamide.ide.plugin.IDEResource;
import com.teamide.ide.plugin.IDEResourceType;
import com.teamide.ide.plugin.IDESpaceListener;

public class AppPlugin implements IDEPlugin {

	@Override
	public String getText() {
		return "应用";
	}

	@Override
	public String getName() {
		return "app";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getOptionType() {
		return "APP";
	}

	@Override
	public String getProjectAttributeName() {
		return "app";
	}

	@Override
	public String getFileAttributeName() {
		return "model";
	}

	@Override
	public List<IDEInput> getOptionInputs() {
		List<IDEInput> inputs = new ArrayList<IDEInput>();
		inputs.add(new IDEInput("模型路径", "modelpath"));
		return inputs;
	}

	@Override
	public List<IDEResource> getResources() {
		List<IDEResource> resources = new ArrayList<IDEResource>();
		resources.add(new IDEResource(IDEResourceType.CSS, "resources/index.css"));
		resources.add(new IDEResource(IDEResourceType.JS, "resources/index.js"));
		return resources;
	}

	@Override
	public String getScriptClassName() {
		return "AppPlugin";
	}

	private IDEListener listener = new AppListener();

	@Override
	public IDEListener getListener() {
		return listener;
	}

	@Override
	public JSONObject getEnumMap() {
		JSONObject ENUM_MAP = new JSONObject();

		JSONArray array = new JSONArray();
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

		return ENUM_MAP;
	}

	@Override
	public IDESpaceListener getSpaceListener() {
		return null;
	}

	@Override
	public IDERepositoryListener getRepositoryListener() {
		return null;
	}

	static AppRepositoryProjectListener projectListener = new AppRepositoryProjectListener();

	@Override
	public IDERepositoryProjectListener getProjectListener() {
		return projectListener;
	}

}
