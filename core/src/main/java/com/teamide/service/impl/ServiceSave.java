package com.teamide.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.teamide.dao.IDao;
import com.teamide.db.BeanTableUtil;
import com.teamide.db.DBDataSource;
import com.teamide.param.SqlParam;
import com.teamide.param.SqlParamList;
import com.teamide.service.IServiceSave;
import com.teamide.util.StringUtil;

public class ServiceSave extends ServiceQuery implements IServiceSave {

	public ServiceSave(DBDataSource dbDataSource, IDao dao) {
		super(dbDataSource, dao);
	}

	public ServiceSave(DBDataSource dbDataSource) {
		super(dbDataSource);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object object, String tablename) throws Exception {

		if (object == null) {
			return null;
		}
		Class<T> clazz = (Class<T>) object.getClass();
		if (StringUtil.isEmpty(tablename)) {
			tablename = getTablename(clazz, object);
		}
		Map<String, Object> data = getColumnData(object);
		String key = getPrimaryKey(clazz);
		return get(clazz, tablename, (Serializable) data.get(key));
	}

	public boolean find(Object object, String tablename) throws Exception {

		if (object == null) {
			return false;
		}
		Class<?> clazz = object.getClass();
		if (StringUtil.isEmpty(tablename)) {
			tablename = getTablename(clazz, object);
		}
		Map<String, Object> data = getColumnData(object);
		String key = getPrimaryKey(clazz);

		return find(clazz, tablename, (Serializable) data.get(key));
	}

	@Override
	public <T> T insert(Object object) throws Exception {

		return insert(object, null);
	}

	@Override
	public <T> T insert(Object object, String tablename) throws Exception {

		executeInsert(object, tablename);
		return get(object, tablename);
	}

	@Override
	public <T> T update(Object object) throws Exception {

		return update(object, null);
	}

	@Override
	public <T> T update(Object object, String tablename) throws Exception {

		executeUpdate(object, tablename);
		return get(object, tablename);
	}

	@Override
	public <T> T insertOrUpdate(Object object) throws Exception {

		return insertOrUpdate(object, null);
	}

	@Override
	public <T> T insertOrUpdate(Object object, String tablename) throws Exception {

		if (find(object, tablename)) {
			Object old = get(object, tablename);
			executeUpdate(object, tablename, old);
		} else {
			executeInsert(object, tablename);
		}
		return get(object, tablename);
	}

	@Override
	public int executeUpdate(Object object) throws Exception {

		return executeUpdate(object, null);
	}

	@Override
	public int executeUpdate(Object object, String tablename) throws Exception {

		return executeUpdate(object, tablename, null);
	}

	public int executeUpdate(Object object, String tablename, Object old) throws Exception {

		if (object instanceof List) {
			return executeUpdates((List<?>) object, tablename);
		}
		SqlParam sqlParam = BeanTableUtil.getUpdate(object, tablename, old, getDBDataSource());
		return execute(sqlParam);
	}

	@Override
	public int executeUpdates(List<?> objects) throws Exception {

		return executeUpdates(objects, null);
	}

	@Override
	public int executeUpdates(List<?> objects, String tablename) throws Exception {

		SqlParamList sqlParamList = new SqlParamList();
		for (Object object : objects) {
			SqlParam sqlParam = BeanTableUtil.getUpdate(object, tablename, null, getDBDataSource());
			sqlParamList.add(sqlParam);
		}
		return execute(sqlParamList);
	}

	@Override
	public int executeUpdate(String tablename, Map<String, Object> keyNameValueMap, Map<String, Object> param)
			throws Exception {

		String sql = BeanTableUtil.getUpdateSql(tablename, keyNameValueMap, param, null, getDialect());
		return execute(sql, param);
	}

	@Override
	public int executeInsert(Object object) throws Exception {

		return executeInsert(object, null);
	}

	@Override
	public int executeInsert(Object object, String tablename) throws Exception {

		if (object instanceof List) {
			return executeInserts((List<?>) object, tablename);
		}
		SqlParam sqlParam = BeanTableUtil.getInsert(object, tablename, getDBDataSource());
		return execute(sqlParam);
	}

	@Override
	public int executeInserts(List<?> objects) throws Exception {

		return executeInserts(objects, null);
	}

	@Override
	public int executeInserts(List<?> objects, String tablename) throws Exception {

		SqlParamList sqlParamList = new SqlParamList();
		for (Object object : objects) {
			SqlParam sqlParam = BeanTableUtil.getInsert(object, tablename, getDBDataSource());
			sqlParamList.add(sqlParam);
		}
		return execute(sqlParamList);
	}

	@Override
	public int executeInsert(String tablename, Map<String, Object> param) throws Exception {

		String sql = BeanTableUtil.getInsertSql(tablename, param, getDialect());
		return execute(sql, param);
	}

	public SqlParamList getInsertOrUpdateSqlParams(List<?> objects, String tablename) throws Exception {

		SqlParamList sqlParamList = new SqlParamList();
		for (Object object : objects) {
			if (find(object, tablename)) {
				SqlParam sqlParam = BeanTableUtil.getUpdate(object, tablename, null, getDBDataSource());
				sqlParamList.add(sqlParam);
			} else {
				SqlParam sqlParam = BeanTableUtil.getInsert(object, tablename, getDBDataSource());
				sqlParamList.add(sqlParam);
			}
		}

		return sqlParamList;
	}

	@Override
	public int executeInsertOrUpdates(List<?> objects) throws Exception {

		return executeInsertOrUpdates(objects, null);
	}

	@Override
	public int executeInsertOrUpdates(List<?> objects, String tablename) throws Exception {

		SqlParamList sqlParamList = getInsertOrUpdateSqlParams(objects, tablename);
		return execute(sqlParamList);
	}
}
