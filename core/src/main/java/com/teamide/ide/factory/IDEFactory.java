package com.teamide.ide.factory;

import java.util.Map;

import com.teamide.db.DataSourceFactory;
import com.teamide.db.TableUtil;
import com.teamide.db.bean.Database;
import com.teamide.factory.DatabaseFactory;
import com.teamide.service.IService;
import com.teamide.util.StringUtil;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.configure.IDEOptions;

public class IDEFactory {

	public static String getDefaultPassword() {

		IDEConfigure setting = IDEConfigure.get();
		String password = null;
		if (setting != null) {
			password = setting.getDefaultpassword();
		}
		if (StringUtil.isEmpty(password)) {
			password = "123456";
		}
		return password;
	}

	public static Database getDatabase() {

		return IDEOptions.get().jdbc;
	}

	public static final String getRealtablename(Class<?> clazz, Map<String, Object> data) throws Exception {

		return TableUtil.getRealtablename(clazz, data, getService().getDao().getDataSourceFactory());
	}

	public static DataSourceFactory getDSFactory() {

		try {
			if (getDatabase() != null) {
				return DataSourceFactory.create(getDatabase());
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static IService getService() {

		try {
			if (getDatabase() != null) {
				return DatabaseFactory.newService(getDatabase());
			}
		} catch (Exception e) {
		}
		return null;
	}

	// static Engine engine = null;
	//
	// static Object lock = new Object();
	//
	// public static Engine getEngine() {
	// if (engine != null) {
	// return engine;
	// }
	// synchronized (lock) {
	// if (engine != null) {
	// return engine;
	// }
	// try {
	// engine = new com.teamide.protect.ide.engine.Engine();
	// } catch (Exception e) {
	// }
	// return engine;
	// }
	// }

}
