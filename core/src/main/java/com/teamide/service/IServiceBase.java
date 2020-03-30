package com.teamide.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.teamide.dao.IDao;
import com.teamide.db.DBDataSource;
import com.teamide.db.dialect.Dialect;
import com.teamide.param.SqlParam;
import com.teamide.param.SqlParamList;

public interface IServiceBase {

	public IDao getDao();

	public DBDataSource getDBDataSource();

	public Dialect getDialect();

	public DataSource getDataSource();

	public boolean find(Class<?> clazz, Serializable id) throws Exception;

	public boolean find(Class<?> clazz, String tablename, Serializable id) throws Exception;

	public boolean find(String tablename, String key, Serializable id) throws Exception;

	public int queryCount(Class<?> clazz, Map<String, Object> param) throws Exception;

	public int queryCount(String sql, Map<String, Object> param) throws Exception;

	public int execute(String sql, Map<String, Object> param) throws Exception;

	public int execute(String sql, Map<String, Object> param, List<Object> keyValues) throws SQLException;

	public int execute(SqlParam sqlParam) throws Exception;

	public int execute(SqlParam sqlParam, List<Object> keyValues) throws SQLException;

	public int execute(SqlParamList sqlParamList) throws Exception;

	public int execute(SqlParamList sqlParamList, List<List<Object>> keyValuesList) throws Exception;

	public String getWhereSql(Class<?> clazz, Map<String, Object> param) throws Exception;
}
