package com.teamide.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.teamide.dao.IDao;
import com.teamide.db.DBDataSource;
import com.teamide.service.IServiceDelete;
import com.teamide.util.StringUtil;

public class ServiceDelete extends ServiceBase implements IServiceDelete {

	public ServiceDelete(DBDataSource dbDataSource, IDao dao) {
		super(dbDataSource, dao);
	}

	public ServiceDelete(DBDataSource dbDataSource) {
		super(dbDataSource);
	}

	@Override
	public <T> int delete(Class<T> clazz, Serializable id, Object object) throws Exception {

		String tablename = getTablename(clazz, object);
		String key = getPrimaryKey(clazz);
		return delete(tablename, key, id);
	}

	@Override
	public int delete(String tablename, String key, Serializable id) throws Exception {

		if (StringUtil.isEmptyIfStr(key)) {
			throw new Exception("主键名称为空，无法删除");
		}
		if (StringUtil.isEmptyIfStr(id)) {
			throw new Exception("主键值为空，无法删除");
		}
		String sql = "DELETE FROM " + tablename + " WHERE " + key + "=:" + key;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(key, id);

		return execute(sql, param);
	}

	@Override
	public <T> int delete(Class<T> clazz, Map<String, Object> param) throws Exception {

		String tablename = getTablename(clazz, param);
		String sql = "DELETE FROM " + tablename + getWhereSql(clazz, param);
		if (sql.indexOf("AND") < 0) {
			throw new Exception("未检测到匹配条件，无法删除");
		}
		return execute(sql, param);
	}
}
