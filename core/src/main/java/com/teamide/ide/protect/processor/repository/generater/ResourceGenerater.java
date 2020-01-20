package com.teamide.ide.protect.processor.repository.generater;

import java.io.File;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DatabaseBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class ResourceGenerater extends Generater {

	protected final JSONObject data = new JSONObject();

	public ResourceGenerater(RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(param, app, context);

	}

	public void init() {

	}

	public void generate() throws Exception {
		param.getLog().info("generate default jdbc code.");

		String jdbcPath = getJdbcPath();
		File resourceFolder = getResourceFolder();

		File jdbcFile = new File(resourceFolder, jdbcPath);

		if (!jdbcFile.exists() && !jdbcFile.getParentFile().exists()) {
			jdbcFile.getParentFile().mkdirs();
		}

		saveJDBCProperties(jdbcFile, context.getJdbc());
	}

	public void saveJDBCProperties(File file, DatabaseBean database) throws Exception {
		param.getLog().info("save jdbc [" + file.getName() + "] properties code.");
		JSONObject json = (JSONObject) JSON.toJSON(database);
		Properties properties = new Properties();
		for (String key : json.keySet()) {
			if (json.get(key) != null) {
				properties.put(key, json.getString(key));
			}
		}
		param.saveProperties(file, properties);

	}

}
