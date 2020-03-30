package com.teamide.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.teamide.dao.IDao;
import com.teamide.db.DBDataSource;
import com.teamide.db.DBUtil;
import com.teamide.db.dialect.Dialect;
import com.teamide.param.SqlValues;
import com.teamide.param.SqlValuesList;
import com.teamide.util.IOUtil;
import com.teamide.util.LogUtil;
import com.teamide.util.ObjectUtil;

public abstract class DaoBase implements IDao {

	Logger logger = LogUtil.get();

	protected DataSource dataSource;

	protected Dialect dialect;

	protected boolean showSql = true;

	public DaoBase() {

	}

	public DaoBase(DBDataSource dbDataSource) {
		if (dbDataSource != null) {
			this.setDataSource(dbDataSource.getDataSource());
			this.setDialect(dbDataSource.getDialect());
			if (dbDataSource.getDatabase() != null) {
				this.showSql = ObjectUtil.isTrue(dbDataSource.getDatabase().getShowsql());
			}
		}
	}

	@Override
	public boolean isShowSql() {
		return this.showSql;
	}

	@Override
	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public String getDataSourceKey() {
		if (this.dataSource == null) {
			return null;
		}
		return "" + this.dataSource.hashCode();
	}

	@Override
	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public boolean showSql() {
		return isShowSql();
	}

	public void log(String sql) {
		if (!showSql()) {
			return;
		}
		logger.info("sql:" + sql);
	}

	public void log(String sql, List<Object> values) {
		if (!showSql()) {
			return;
		}
		logger.info("sql:" + sql);
		logger.info("values:" + JSON.toJSONString(values));
	}

	public void batchLog(List<String> sqls) {
		if (!showSql()) {
			return;
		}
		logger.info("batch sql:" + JSON.toJSONString(sqls));
	}

	public void batchLog(String sql, List<List<Object>> valuesList) {
		if (!showSql()) {
			return;
		}
		logger.info("batch sql:" + sql);
		logger.info("batch values list:" + JSON.toJSONString(valuesList));
	}

	public void batchLog(SqlValuesList sqlValuesList) {
		if (!showSql()) {
			return;
		}
		if (sqlValuesList == null) {
			return;
		}
		for (SqlValues sqlValues : sqlValuesList) {
			logger.info("batch sql:" + sqlValues.getSql());
			logger.info("batch values:" + JSON.toJSONString(sqlValues.getValues()));
		}

	}

	public void error(String sql) {
		logger.error("error sql:" + sql);
	}

	public void error(String sql, List<Object> values) {
		logger.error("error sql:" + sql);
		logger.error("error values:" + JSON.toJSONString(values));
	}

	public void batchError(List<String> sqls) {
		logger.error("error batch sql:" + JSON.toJSONString(sqls));
	}

	public void batchError(String sql, List<List<Object>> valuesList) {
		logger.error("error batch sql:" + sql);
		logger.error("error batch values list:" + JSON.toJSONString(valuesList));
	}

	public void batchError(SqlValuesList sqlValuesList) {

		if (sqlValuesList == null) {
			return;
		}
		for (SqlValues sqlValues : sqlValuesList) {
			logger.error("error batch sql:" + sqlValues.getSql());
			logger.error("error batch values:" + JSON.toJSONString(sqlValues.getValues()));
		}
	}

	@Override
	public Dialect getDialect() {

		return dialect;
	}

	@Override
	public DataSource getDataSource() {

		return dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {

		return dataSource.getConnection();
	}

	@Override
	public void close(AutoCloseable... closeables) {
		IOUtil.close(closeables);
	}

	@Override
	public int execute(String sql) throws SQLException {
		return execute(sql, new ArrayList<Object>(), null);
	}

	public List<Map<String, Object>> query(String sql, List<Object> values) throws SQLException {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			log(sql, values);
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			DBUtil.setPreparedStatementValues(ps, values);
			rs = ps.executeQuery();
			result = DBUtil.toList(rs);
		} catch (SQLException e) {
			error(sql, values);
			throw e;
		} finally {
			IOUtil.close(rs, ps, conn);
		}
		return result;
	}

	/**
	 * 
	 * 统计
	 * 
	 * @param sql
	 *            SQL语句
	 * @param values
	 *            ?占位符对应的数据
	 * @return 统计结果
	 * @throws SQLException
	 *             SQL异常
	 */
	public int queryCount(String sql, List<Object> values) throws SQLException {

		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			log(sql, values);
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			DBUtil.setPreparedStatementValues(ps, values);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
				break;
			}
		} catch (SQLException e) {
			error(sql, values);
			throw e;
		} finally {
			IOUtil.close(rs, ps, conn);
		}
		return result;
	}

	@Override
	public int batchExecute(SqlValuesList sqlValuesList) throws SQLException {

		return batchExecute(sqlValuesList, null);
	}

	@Override
	public int batchExecute(List<String> sqls) throws SQLException {

		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);// 将自动提交关闭
			int res = batchExecute(conn, sqls);
			conn.commit();// 执行完后，手动提交事务
			return res;
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);// 在把自动提交打开
				IOUtil.close(conn);
			}
		}
	}

	protected int execute(String sql, List<Object> values, List<Object> keyValues) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);// 将自动提交关闭
			int res = execute(conn, sql, values, keyValues);
			conn.commit();// 执行完后，手动提交事务
			return res;
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);// 在把自动提交打开
				IOUtil.close(conn);
			}
		}
	}

	public int batchExecute(String sql, List<List<Object>> valuesList) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);// 将自动提交关闭
			int res = batchExecute(conn, sql, valuesList);
			conn.commit();// 执行完后，手动提交事务
			return res;
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);// 在把自动提交打开
				IOUtil.close(conn);
			}
		}
	}

	public int batchExecute(SqlValuesList sqlValuesList, List<List<Object>> keyValuesList) throws SQLException {

		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);// 将自动提交关闭
			int res = batchExecute(conn, sqlValuesList, keyValuesList);
			conn.commit();
			return res;
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);// 在把自动提交打开
				IOUtil.close(conn);
			}
		}
	}

	public int batchExecute(Connection conn, List<String> sqls) throws SQLException {
		try {
			batchLog(sqls);
			return DBUtil.batchExecute(conn, sqls);
		} catch (Exception e) {
			batchError(sqls);
			throw e;
		}
	}

	public int execute(Connection conn, String sql, List<Object> values, List<Object> keyValues) throws SQLException {
		try {
			log(sql, values);
			return DBUtil.execute(conn, sql, values, keyValues);
		} catch (Exception e) {
			error(sql, values);
			throw e;
		}
	}

	public int batchExecute(Connection conn, String sql, List<List<Object>> valuesList) throws SQLException {
		try {
			batchLog(sql, valuesList);
			return DBUtil.batchExecute(conn, sql, valuesList);
		} catch (Exception e) {
			batchError(sql, valuesList);
			throw e;
		}
	}

	public int batchExecute(Connection conn, SqlValuesList sqlValuesList, List<List<Object>> keyValuesList)
			throws SQLException {
		try {
			batchLog(sqlValuesList);
			return DBUtil.batchExecute(conn, sqlValuesList, keyValuesList);
		} catch (Exception e) {
			batchError(sqlValuesList);
			throw e;
		}
	}
}
