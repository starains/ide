package com.teamide.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.teamide.bean.PageResultBean;
import com.teamide.dao.IDao;
import com.teamide.db.DBDataSource;
import com.teamide.db.DBUtil;
import com.teamide.param.PageSqlParam;
import com.teamide.param.SqlParam;
import com.teamide.param.SqlParamList;
import com.teamide.param.SqlValues;

/**
 * Dao层实现，需要传入数据源
 * 
 * @author ZhuLiang
 *
 */
public class Dao extends DaoBase implements IDao {

	public Dao() {
		super();
	}

	public Dao(DBDataSource dbDataSource) {
		super(dbDataSource);
	}

	/**
	 * 查询
	 */
	@Override
	public List<Map<String, Object>> query(SqlParam sqlParam) throws SQLException {

		return query(DBUtil.toSqlValues(sqlParam));

	}

	/**
	 * 查询
	 */
	@Override
	public List<Map<String, Object>> query(SqlValues sqlValues) throws SQLException {

		return query(sqlValues.getSql(), sqlValues.getValues());

	}

	/**
	 * 统计
	 */
	@Override
	public int queryCount(SqlParam sqlParam) throws SQLException {

		return queryCount(DBUtil.toSqlValues(sqlParam));

	}

	/**
	 * 统计
	 */
	@Override
	public int queryCount(SqlValues sqlValues) throws SQLException {

		return queryCount(sqlValues.getSql(), sqlValues.getValues());
	}

	/**
	 * 查询列表
	 */
	@Override
	public List<Map<String, Object>> queryList(SqlParam sqlParam) throws SQLException {

		return queryList(DBUtil.toSqlValues(sqlParam));
	}

	/**
	 * 查询列表
	 */
	@Override
	public List<Map<String, Object>> queryList(SqlValues sqlValues) throws SQLException {

		return query(sqlValues);
	}

	/**
	 * 查询单个
	 */
	@Override
	public Map<String, Object> queryOne(SqlParam sqlParam) throws SQLException {

		return queryOne(DBUtil.toSqlValues(sqlParam));
	}

	/**
	 * 查询单个
	 */
	@Override
	public Map<String, Object> queryOne(SqlValues sqlValues) throws SQLException {

		List<Map<String, Object>> value = query(sqlValues);
		if (value == null) {
			return null;
		}
		if (value.size() > 0) {
			return value.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 翻页查询
	 */
	@Override
	public PageResultBean<Map<String, Object>> queryPage(PageSqlParam pageSqlParam) throws SQLException {
		String pageSql = getDialect().wrapPageSql(pageSqlParam.getSql(), pageSqlParam);

		List<Map<String, Object>> value = query(new SqlParam(pageSql, pageSqlParam.getParam()));
		int totalcount = queryCount(new SqlParam(pageSqlParam.getCountsql(), pageSqlParam.getParam()));

		PageResultBean<Map<String, Object>> result = new PageResultBean<Map<String, Object>>();
		result.setPageindex(pageSqlParam.getPageindex());
		result.setPagesize(pageSqlParam.getPagesize());
		result.setTotalcount(totalcount);
		result.setValue(value);
		return result;
	}

	@Override
	public int execute(SqlParam sqlParam) throws SQLException {
		return execute(DBUtil.toSqlValues(sqlParam));
	}

	@Override
	public int execute(SqlParam sqlParam, List<Object> keyValues) throws SQLException {
		SqlValues sqlValues = DBUtil.toSqlValues(sqlParam);
		return execute(sqlValues.getSql(), sqlValues.getValues(), keyValues);
	}

	@Override
	public int execute(SqlValues sqlValues) throws SQLException {

		return execute(sqlValues.getSql(), sqlValues.getValues(), null);
	}

	@Override
	public int batchExecute(SqlParamList sqlParamList) throws SQLException {

		return batchExecute(DBUtil.toSqlValuesList(sqlParamList), null);
	}

	@Override
	public int batchExecute(SqlParamList sqlParamList, List<List<Object>> keyValuesList) throws SQLException {

		return batchExecute(DBUtil.toSqlValuesList(sqlParamList), keyValuesList);
	}

}
