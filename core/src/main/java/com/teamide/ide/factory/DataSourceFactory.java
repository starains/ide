package com.teamide.ide.factory;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.teamide.dao.IDao;
import com.teamide.dao.impl.Dao;
import com.teamide.db.DBDataSource;
import com.teamide.db.bean.Database;
import com.teamide.service.IService;
import com.teamide.service.impl.Service;
import com.teamide.util.StringUtil;

/**
 * 数据库工厂
 * 
 * @author ZhuLiang
 *
 */
public class DataSourceFactory {

	private final DatabaseFactory databaseFactory;

	public DataSourceFactory(DatabaseFactory databaseFactory) {
		this.databaseFactory = databaseFactory;
	}

	public DBDataSource get(JSONObject data) throws Exception {
		return get("", data);

	}

	public DBDataSource get(String name, JSONObject data) throws Exception {
		Database database = databaseFactory.getRealDatabase(name, data);
		return getDBDataSource(database);
	}

	public IDao newDao(JSONObject data) throws Exception {
		return newDao("", data);
	}

	public IDao newDao(String name, JSONObject data) throws Exception {
		return new Dao(get(name, data));
	}

	public IService newService(JSONObject data) throws Exception {
		return newService("", data);
	}

	public IService newService(String name, JSONObject data) throws Exception {
		return new Service(get(name, data));
	}

	/**
	 * 根据数据库配置获取key，以数据库配置的驱动、连接、用户名、密码作为key拼接
	 * 
	 * @param database
	 *            数据库配置
	 * @return 数据库配置生成的Key
	 */
	public static String getKey(Database database) {

		String key = "DATABASE";
		if (database != null) {
			if (StringUtil.isNotEmpty(database.getDriver())) {
				key += "-" + database.getDriver();
			}
			if (StringUtil.isNotEmpty(database.getUrl())) {
				key += "-" + database.getUrl();
			}
			if (StringUtil.isNotEmpty(database.getUsername())) {
				key += "-" + database.getUsername();
			}
			if (StringUtil.isNotEmpty(database.getPassword())) {
				key += "-" + database.getPassword();
			}
		}

		return key;
	}

	public static final Map<String, DBDataSource> DATASOURCE_CACHE = new HashMap<String, DBDataSource>();

	private static final Object DATASOURCE_CACHE_LOCK = new Object();

	/**
	 * 根据数据库配置获取DataSource，并且缓存起来，如果这个database已经创建过数据源，则直接返回已经创建的
	 * 
	 * @param database
	 *            数据库配置
	 * @return 数据库对应的数据源
	 */
	public static DBDataSource getDBDataSource(Database database) throws Exception {
		String key = getKey(database);
		DBDataSource dataSourceFactory = DATASOURCE_CACHE.get(key);
		if (dataSourceFactory == null) {
			synchronized (DATASOURCE_CACHE_LOCK) {
				dataSourceFactory = DATASOURCE_CACHE.get(key);
				if (dataSourceFactory == null) {
					dataSourceFactory = DBDataSource.create(database);
					DATASOURCE_CACHE.put(key, dataSourceFactory);
				}
			}
		}
		return dataSourceFactory;
	}

	public static void destroy(Database database) {
		String key = getKey(database);
		DBDataSource dataSourceFactory = DATASOURCE_CACHE.get(key);
		if (dataSourceFactory != null) {
			dataSourceFactory.destroy();
			DATASOURCE_CACHE.remove(key);
		}
	}

}
