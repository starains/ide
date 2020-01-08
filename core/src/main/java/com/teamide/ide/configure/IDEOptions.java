package com.teamide.ide.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.db.bean.Database;
import com.teamide.util.StringUtil;
import com.teamide.ide.constant.IDEConstant;

public class IDEOptions {

	private static IDEOptions OPTIONS;

	public static IDEOptions get() {

		if (OPTIONS == null) {
			loadOptions();
		}
		return OPTIONS;
	}

	public static final IDEOptions loadOptions() {

		OPTIONS = new IDEOptions();

		Properties config = loadConfig();
		JSONObject json = (JSONObject) JSON.toJSON(config);
		OPTIONS = json.toJavaObject(IDEOptions.class);

		Properties jdbc = loadJdbc();
		json = (JSONObject) JSON.toJSON(jdbc);
		Database database = json.toJavaObject(Database.class);
		if (database != null && !StringUtil.isEmpty(database.getUrl())) {
			OPTIONS.jdbc = database;
		}

		return OPTIONS;
	}

	public static final Properties loadConfig() {
		return load(IDEConstant.CONFIG);
	}

	public static final Properties loadJdbc() {
		return load(IDEConstant.JDBC);
	}

	public static final Properties load(String path) {

		Properties props = new Properties();
		FileInputStream stream = null;
		try {
			if (new File(path).isFile()) {
				stream = new FileInputStream(path);
				props.load(stream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception e) {
			}
		}

		return props;
	}

	public Database jdbc;

	public Database getJdbc() {

		return jdbc;
	}

	public void setJdbc(Database jdbc) {

		this.jdbc = jdbc;
	}

}
