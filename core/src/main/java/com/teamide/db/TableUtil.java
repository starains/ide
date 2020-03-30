package com.teamide.db;

import java.util.HashMap;
import java.util.Map;

import com.teamide.db.bean.Table;
import com.teamide.db.generater.TableGenerater;

public class TableUtil {

	static final Map<String, Boolean> TABLE_GENERATE_CACHE = new HashMap<String, Boolean>();

	public static String getRealtablename(Class<?> clazz, DBDataSource dbDataSource) throws Exception {

		return getRealtablename(clazz, null, dbDataSource);
	}

	public static String getRealtablename(Class<?> clazz, Map<String, Object> data, DBDataSource dbDataSource)
			throws Exception {

		return getRealtablename(BeanTableUtil.getTable(clazz), data, dbDataSource);
	}

	public static String getRealtablename(Table table, Map<String, Object> data, DBDataSource dbDataSource)
			throws Exception {

		// 获取真实表名
		String realtablename = PartitionTableUtil.getTablename(table, data);
		// 生成表
		String key = getKey(table, data, dbDataSource);
		if (TABLE_GENERATE_CACHE.get(key) == null) {
			generate(realtablename, table, dbDataSource);
			TABLE_GENERATE_CACHE.put(key, true);
		}

		return realtablename;
	}

	public static String getKey(Table table, Map<String, Object> data, DBDataSource dbDataSource)
			throws Exception {

		// 获取真实表名
		String realtablename = PartitionTableUtil.getTablename(table, data);
		// 生成表
		String key = realtablename;
		if (dbDataSource.getDatabase() != null) {
			key = dbDataSource.getDatabase().getUrl() + "-" + realtablename;
		}
		return key;
	}

	public static void generate(String tablename, Table table, DBDataSource dbDataSource) throws Exception {

		TableGenerater generater = new TableGenerater(dbDataSource);
		generater.generate(tablename, table);
		String key = getKey(table, null, dbDataSource);
		TABLE_GENERATE_CACHE.put(key, true);

	}

	public static void cleanTableCache() {
		TABLE_GENERATE_CACHE.clear();
	}
}
