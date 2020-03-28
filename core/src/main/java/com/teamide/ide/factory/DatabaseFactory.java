package com.teamide.ide.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.dao.IDao;
import com.teamide.db.bean.Database;
import com.teamide.db.ifaces.IDatabaseInitialize;
import com.teamide.exception.DatabaseInitializeException;
import com.teamide.exception.DatabaseNotFoundException;
import com.teamide.service.IService;
import com.teamide.util.PropertyUtil;
import com.teamide.util.StringUtil;

/**
 * 数据库工厂
 * 
 * @author ZhuLiang
 *
 */
public class DatabaseFactory {

	private final Map<String, Database> load_cache = new HashMap<String, Database>();

	private final String default_jdbc;

	private final String jdbc_directory;

	private final DataSourceFactory dataSourceFactory;

	/**
	 * 可以传入默认JDBC和JDBC目录初始化一个工厂，实例化后可以根据名称直接获取数据源、Dao等
	 * 
	 * @param default_jdbc
	 *            默认JDBC配置文件路径，名称为null或者空时候使用该配置
	 * @param jdbc_directory
	 *            JDBC目录路径，可以使用名称获取
	 */
	public DatabaseFactory(String default_jdbc, String jdbc_directory) {
		if (StringUtil.isEmpty(default_jdbc)) {
			default_jdbc = "jdbc";
		}
		if (StringUtil.isEmpty(jdbc_directory)) {
			jdbc_directory = "database";
		}
		this.default_jdbc = default_jdbc;
		this.jdbc_directory = jdbc_directory;
		this.dataSourceFactory = new DataSourceFactory(this);
	}

	public Database getDatabase() throws Exception {
		return getDatabase("");

	}

	public Database getDatabase(String name) throws Exception {
		Database database = load_cache.get(name);
		if (database == null) {
			String jdbc_property = default_jdbc;
			if (StringUtil.isNotEmpty(name)) {
				jdbc_property = jdbc_directory + "/" + name;
			}
			if (!jdbc_property.endsWith(".properties")) {
				jdbc_property = jdbc_property + ".properties";
			}
			database = load(jdbc_property);
			load_cache.put(name, database);
		}

		return database;
	}

	public Database getRealDatabase(JSONObject data) throws Exception {
		return getRealDatabase("", data);

	}

	public Database getRealDatabase(String name, JSONObject data) throws Exception {
		Database database = getDatabase(name);
		return getRealDatabase(database, data);
	}

	public IDao newDao(JSONObject data) throws Exception {
		return newDao("", data);
	}

	public IDao newDao(String name, JSONObject data) throws Exception {
		return this.dataSourceFactory.newDao(name, data);
	}

	public IService newService(JSONObject data) throws Exception {
		return newService("", data);
	}

	public IService newService(String name, JSONObject data) throws Exception {
		return this.dataSourceFactory.newService(name, data);
	}

	public static Database getRealDatabase(Database database, JSONObject data) throws Exception {
		if (database == null) {
			return null;
		}
		if (StringUtil.isTrimEmpty(database.getInitializeclass())) {
			return database;
		}
		String initializeclass = database.getInitializeclass();
		try {
			Class<?> clazz = Class.forName(initializeclass.trim());
			Object obj = clazz.newInstance();
			IDatabaseInitialize databaseInitialize = (IDatabaseInitialize) obj;
			return databaseInitialize.initialize(database, data);

		} catch (ClassNotFoundException e) {
			throw new DatabaseInitializeException("database initialize class [" + initializeclass + "] not found.");
		}
	}

	/**
	 * 可以传入默认JDBC和JDBC目录初始化一个工厂，实例化后可以根据名称直接获取数据源、Dao等
	 * 
	 * @return DatabaseFactory
	 */
	public static DatabaseFactory getFactory() {
		return getFactory(null, null);
	}

	/**
	 * 可以传入默认JDBC和JDBC目录初始化一个工厂，实例化后可以根据名称直接获取数据源、Dao等
	 * 
	 * @param default_jdbc
	 *            默认JDBC配置文件路径，名称为null或者空时候使用该配置
	 * @param jdbc_directory
	 *            JDBC目录路径，可以使用名称获取
	 * @return DatabaseFactory
	 */
	public static DatabaseFactory getFactory(String default_jdbc, String jdbc_directory) {
		return new DatabaseFactory(default_jdbc, jdbc_directory);
	}

	/**
	 * 加载配置文件，转数据库配置对象
	 * 
	 * @param jdbc_property
	 *            JDBC配置文件
	 * @return 数据库配置
	 */
	public static Database load(String jdbc_property) throws DatabaseNotFoundException {
		Properties properties = null;
		try {
			properties = PropertyUtil.get(jdbc_property);
		} catch (Exception e) {
		}
		if (properties == null) {
			throw new DatabaseNotFoundException("jdbc property [" + jdbc_property + "] does not exist.");
		}
		if (properties.size() == 0) {
			throw new DatabaseNotFoundException("jdbc property [" + jdbc_property + "] error.");
		}
		JSONObject json = (JSONObject) JSON.toJSON(properties);
		return json.toJavaObject(Database.class);
	}
}
