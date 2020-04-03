package com.teamide.app.generater.resources;

import java.io.File;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DatabaseBean;
import com.teamide.app.generater.Generater;
import com.teamide.app.plugin.AppBean;
import com.teamide.db.bean.Database;

public class ResourcesGenerater extends Generater {

	protected final JSONObject data = new JSONObject();

	public ResourcesGenerater(File sourceFolder, AppBean app, AppContext context) {
		super(sourceFolder, app, context);

	}

	public void init() {

	}

	public void generate() throws Exception {
		// param.getLog().info("generate default jdbc code.");

		// String jdbcPath = getJdbcPath();
		// File resourceFolder = getResourceFolder();
		//
		// File jdbcFile = new File(resourceFolder, jdbcPath);
		//
		// if (!jdbcFile.exists() && !jdbcFile.getParentFile().exists()) {
		// jdbcFile.getParentFile().mkdirs();
		// }
		//
		// saveJDBCProperties(jdbcFile, context.getJdbc());
		//
		// List<DatabaseBean> databases = context.get(DatabaseBean.class);
		// if (databases.size() > 0) {
		//
		// File jdbcDirectory = new File(resourceFolder, getJdbcsDirectory());
		// if (!jdbcDirectory.exists()) {
		// jdbcDirectory.mkdirs();
		// }
		// for (DatabaseBean database : databases) {
		// File jdbcDirectoryFile = new File(jdbcDirectory, database.getName() +
		// ".properties");
		// saveJDBCProperties(jdbcDirectoryFile, database);
		// }
		// }
	}

	public void saveJDBCProperties(File file, DatabaseBean database) throws Exception {

		// param.getLog().info("save jdbc [" + file.getName() + "] properties
		// code.");
		JSONObject json = (JSONObject) JSON.toJSON(database);

		Database db = json.toJavaObject(Database.class);
		json = (JSONObject) JSON.toJSON(db);

		Properties properties = new Properties();
		for (String key : json.keySet()) {
			if (json.get(key) != null) {
				properties.put(key, json.getString(key));
			}
		}
		// param.saveProperties(file, properties);

	}

}
