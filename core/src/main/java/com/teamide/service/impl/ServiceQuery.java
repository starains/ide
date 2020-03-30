package com.teamide.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.bean.PageResultBean;
import com.teamide.dao.IDao;
import com.teamide.db.DBDataSource;
import com.teamide.param.PageSqlParam;
import com.teamide.param.SqlParam;
import com.teamide.service.IServiceQuery;
import com.teamide.util.StringUtil;

public class ServiceQuery extends ServiceDelete implements IServiceQuery {

	public ServiceQuery(DBDataSource dbDataSource, IDao dao) {
		super(dbDataSource, dao);
	}

	public ServiceQuery(DBDataSource dbDataSource) {
		super(dbDataSource);
	}

	@Override
	public <T> T get(Class<T> clazz, Serializable id) throws Exception {

		return get(clazz, null, id);
	}

	@Override
	public <T> T get(Class<T> clazz, String tablename, Serializable id) throws Exception {

		String key = getPrimaryKey(clazz);
		if (StringUtil.isEmpty(tablename)) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(key, id);
			tablename = getTablename(clazz, param);
		}
		return get(clazz, tablename, key, id);
	}

	@Override
	public <T> T get(Class<T> clazz, String tablename, String key, Serializable id) throws Exception {

		Map<String, Object> one = get(tablename, key, id);

		return toColumnObject(clazz, one);
	}

	@Override
	public Map<String, Object> get(String tablename, String key, Serializable id) throws Exception {

		String sql = "SELECT * FROM " + tablename + " WHERE " + key + "=:" + key;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(key, id);
		return queryOne(sql, param);
	}

	@Override
	public Map<String, Object> queryOne(String sql, Map<String, Object> param) throws Exception {

		return dao.queryOne(new SqlParam(sql, param));
	}

	@Override
	public <T> T queryOne(Class<T> clazz, Map<String, Object> param) throws Exception {

		String completetablename = getTablename(clazz, param);
		String sql = "SELECT * FROM " + completetablename + getWhereSql(clazz, param);
		return queryOne(clazz, sql, param);
	}

	@Override
	public <T> T queryOne(Class<T> clazz, String sql, Map<String, Object> param) throws Exception {

		Map<String, Object> one = queryOne(sql, param);
		return toColumnObject(clazz, one);
	}

	@Override
	public List<Map<String, Object>> queryList(SqlParam sqlParam) throws Exception {

		if (sqlParam != null) {
			return dao.queryList(sqlParam);
		}
		return new ArrayList<Map<String, Object>>();
	}

	@Override
	public <T> List<T> queryList(Class<T> clazz, Map<String, Object> param) throws Exception {

		String completetablename = getTablename(clazz, param);
		String sql = "SELECT * FROM " + completetablename + getWhereSql(clazz, param);
		return queryList(clazz, sql, param);
	}

	@Override
	public List<Map<String, Object>> queryList(String sql, Map<String, Object> param) throws Exception {

		return dao.queryList(new SqlParam(sql, param));
	}

	@Override
	public <T> List<T> queryList(Class<T> clazz, String sql, Map<String, Object> param) throws Exception {

		List<Map<String, Object>> datas = queryList(sql, param);
		List<T> list = new ArrayList<T>();
		if (datas != null) {
			for (Map<String, Object> data : datas) {
				T t = toColumnObject(clazz, data);
				list.add(t);
			}
		}
		return list;
	}

	@Override
	public PageResultBean<Map<String, Object>> queryPageResult(PageSqlParam pageSqlParam) throws Exception {
		return dao.queryPage(pageSqlParam);
	}

	@Override
	public <T> PageResultBean<T> queryPageResult(Class<T> clazz, PageSqlParam pageSqlParam) throws Exception {

		PageResultBean<T> result = new PageResultBean<T>();

		PageResultBean<Map<String, Object>> queryResult = queryPageResult(pageSqlParam);
		List<T> list = new ArrayList<T>();
		if (queryResult != null) {
			result.setPageindex(queryResult.getPageindex());
			result.setPagesize(queryResult.getPagesize());
			result.setTotalcount(queryResult.getTotalcount());
			result.setTotalpages(queryResult.getTotalpages());
			List<Map<String, Object>> datas = queryResult.getValue();
			if (datas != null) {
				for (Map<String, Object> data : datas) {
					T t = toColumnObject(clazz, data);
					list.add(t);
				}
			}
		}
		result.setValue(list);
		return result;
	}

}
