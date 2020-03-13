package com.teamide.ide.generater.factory;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DatabaseBean;
import com.teamide.ide.generater.BaseGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class FactoryGenerater extends BaseGenerater {

	public FactoryGenerater(RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(null, param, app, context);
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		return getFactoryPackage();
	}

	@Override
	public void buildData() {

		JSONArray $databases = new JSONArray();
		data.put("$databases", $databases);
		List<DatabaseBean> databases = context.get(DatabaseBean.class);
		for (DatabaseBean database : databases) {
			JSONObject one = (JSONObject) JSONObject.toJSON(database);
			$databases.add(one);
		}

	}

	public String getClassName() {
		return getAppFactoryClassname();
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/factory";
	}

}
