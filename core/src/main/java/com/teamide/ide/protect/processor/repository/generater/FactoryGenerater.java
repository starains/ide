package com.teamide.ide.protect.processor.repository.generater;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DatabaseBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class FactoryGenerater extends CodeGenerater {

	public FactoryGenerater(RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(null, param, app, context);
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		return getAppFactoryPackage();
	}

	@Override
	public void buildData() {

		data.put("$package", getAppFactoryPackage());
		data.put("$classname", getAppFactoryClassname());
		JSONArray $databases = new JSONArray();
		data.put("$databases", $databases);
		List<DatabaseBean> databases = context.get(DatabaseBean.class);
		for (DatabaseBean database : databases) {
			JSONObject one = (JSONObject) JSONObject.toJSON(database);
			$databases.add(one);
		}

	}

	public String getClassName() {
		return "AppFactory";
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/factory";
	}

}
