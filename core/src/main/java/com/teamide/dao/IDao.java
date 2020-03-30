package com.teamide.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.teamide.bean.PageResultBean;
import com.teamide.db.dialect.Dialect;
import com.teamide.param.PageSqlParam;
import com.teamide.param.SqlParam;
import com.teamide.param.SqlParamList;
import com.teamide.param.SqlValues;
import com.teamide.param.SqlValuesList;

/**
 * 数据访问接口、用于执行SQL
 * 
 * @author ZhuLiang
 *
 */
public interface IDao {

	public boolean isShowSql();

	public void setShowSql(boolean showSql);

	public String getDataSourceKey();

	public DataSource getDataSource();

	public void setDataSource(DataSource dataSource);

	public Dialect getDialect();

	public void setDialect(Dialect dialect);

	public Connection getConnection() throws SQLException;

	public List<Map<String, Object>> query(SqlValues sqlValues) throws SQLException;

	public List<Map<String, Object>> query(SqlParam sqlParam) throws SQLException;

	public Map<String, Object> queryOne(SqlValues sqlValues) throws SQLException;

	public Map<String, Object> queryOne(SqlParam sqlParam) throws SQLException;

	public List<Map<String, Object>> queryList(SqlValues sqlValues) throws SQLException;

	public List<Map<String, Object>> queryList(SqlParam sqlParam) throws SQLException;

	public int queryCount(SqlValues sqlValues) throws SQLException;

	public int queryCount(SqlParam sqlParam) throws SQLException;

	public PageResultBean<Map<String, Object>> queryPage(PageSqlParam pageSqlParam) throws SQLException;

	public int execute(String sql) throws SQLException;

	public int execute(SqlParam sqlParam) throws SQLException;

	public int execute(SqlParam sqlParam, List<Object> keyValues) throws SQLException;

	public int execute(SqlValues sqlValues) throws SQLException;

	public int batchExecute(List<String> sqls) throws SQLException;

	public int batchExecute(SqlParamList sqlParamList) throws SQLException;

	public int batchExecute(SqlParamList sqlParamList, List<List<Object>> keyValuesList) throws SQLException;

	public int batchExecute(SqlValuesList sqlValuesList) throws SQLException;

	public int batchExecute(String sql, List<List<Object>> valuesList) throws SQLException;

	public void close(AutoCloseable... closeables);

}
