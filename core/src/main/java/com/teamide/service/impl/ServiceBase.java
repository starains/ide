package com.teamide.service.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.teamide.dao.IDao;
import com.teamide.dao.impl.Dao;
import com.teamide.db.BeanTableUtil;
import com.teamide.db.DBDataSource;
import com.teamide.db.TableUtil;
import com.teamide.db.bean.Column;
import com.teamide.db.bean.Table;
import com.teamide.db.dialect.Dialect;
import com.teamide.param.SqlParam;
import com.teamide.param.SqlParamList;
import com.teamide.service.IServiceBase;
import com.teamide.util.StringUtil;

public class ServiceBase implements IServiceBase {

	protected final IDao dao;
	protected final DBDataSource dbDataSource;

	public ServiceBase(DBDataSource dbDataSource, IDao dao) {
		this.dao = dao;
		this.dbDataSource = dbDataSource;
	}

	public ServiceBase(DBDataSource dbDataSource) {
		this.dao = new Dao(dbDataSource);
		this.dbDataSource = dbDataSource;
	}

	@Override
	public IDao getDao() {

		return dao;
	}

	public DBDataSource getDBDataSource() {
		return this.dbDataSource;
	}

	@Override
	public DataSource getDataSource() {

		return dao.getDataSource();
	}

	public Dialect getDialect() {

		return dao.getDialect();
	}

	@Override
	public int execute(String sql, Map<String, Object> param) throws Exception {

		return dao.execute(new SqlParam(sql, param));
	}

	@Override
	public int execute(String sql, Map<String, Object> param, List<Object> keyValues) throws SQLException {

		return dao.execute(new SqlParam(sql, param), keyValues);
	}

	@Override
	public int execute(SqlParam sqlParam) throws Exception {

		return dao.execute(sqlParam);
	}

	@Override
	public int execute(SqlParam sqlParam, List<Object> keyValues) throws SQLException {

		return dao.execute(sqlParam, keyValues);
	}

	@Override
	public int execute(SqlParamList sqlParamList) throws Exception {

		return dao.batchExecute(sqlParamList);
	}

	@Override
	public int execute(SqlParamList sqlParamList, List<List<Object>> keyValuesList) throws Exception {

		return dao.batchExecute(sqlParamList, keyValuesList);
	}

	public Map<String, Object> getColumnData(Object data) throws Exception {

		return BeanTableUtil.getColumnData(data);
	}

	public <T> T toColumnObject(Class<T> clazz, Map<String, Object> data) {

		return BeanTableUtil.toColumnObject(clazz, data);
	}

	public String getTablename(Class<?> clazz, Object data) throws Exception {

		String tablename = TableUtil.getRealtablename(clazz, BeanTableUtil.getColumnData(data), getDBDataSource());
		return tablename;
	}

	public String getTablename(Class<?> clazz, Map<String, Object> data) throws Exception {

		String tablename = TableUtil.getRealtablename(clazz, data, getDBDataSource());
		return tablename;
	}

	public Set<String> getPrimaryKeys(Class<?> clazz) throws Exception {

		Set<String> keys = BeanTableUtil.getPrimaryKeys(clazz).keySet();
		return keys;
	}

	public String getPrimaryKey(Class<?> clazz) throws Exception {

		Set<String> keys = getPrimaryKeys(clazz);
		return keys.iterator().next();
	}

	public Table getTable(Class<?> clazz) throws Exception {

		Table table = BeanTableUtil.getTable(clazz);
		return table;
	}

	@Override
	public String getWhereSql(Class<?> clazz, Map<String, Object> param) throws Exception {

		String wheresql = " WHERE 1=1 ";
		Table table = getTable(clazz);
		if (param != null) {
			if (table != null && table.getColumns().size() > 0) {
				for (Column column : table.getColumns()) {
					if (!StringUtil.isEmptyIfStr(param.get(column.getName()))) {
						wheresql += " AND " + column.getName() + "=:" + column.getName();
					}
				}
			}
		}
		return wheresql;
	}

	@Override
	public boolean find(Class<?> clazz, Serializable id) throws Exception {

		return find(clazz, null, id);
	}

	@Override
	public boolean find(Class<?> clazz, String tablename, Serializable id) throws Exception {

		String key = getPrimaryKey(clazz);
		if (StringUtil.isEmpty(tablename)) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(key, id);
			tablename = getTablename(clazz, param);
		}
		return find(tablename, key, id);
	}

	@Override
	public boolean find(String tablename, String key, Serializable id) throws Exception {

		String sql = "SELECT COUNT(1) FROM " + tablename + " WHERE " + key + "=:" + key;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(key, id);
		return queryCount(sql, param) > 0;
	}

	@Override
	public int queryCount(String sql, Map<String, Object> param) throws Exception {

		return dao.queryCount(new SqlParam(sql, param));
	}

	@Override
	public int queryCount(Class<?> clazz, Map<String, Object> param) throws Exception {

		String completetablename = getTablename(clazz, param);
		String sql = "select COUNT(1) FROM " + completetablename + getWhereSql(clazz, param);
		return queryCount(sql, param);
	}
}
