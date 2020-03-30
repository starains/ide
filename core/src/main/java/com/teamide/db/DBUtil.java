package com.teamide.db;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teamide.db.bean.Database;
import com.teamide.db.enums.DBType;
import com.teamide.param.SqlParam;
import com.teamide.param.SqlParamList;
import com.teamide.param.SqlValues;
import com.teamide.param.SqlValuesList;
import com.teamide.util.IOUtil;
import com.teamide.util.StringUtil;

/**
 * 数据库工具类
 * 
 * @author ZhuLiang
 *
 */
public class DBUtil {

	/**
	 * 创建数据源
	 * 
	 * @param database
	 *            数据库配置
	 * @return 数据源
	 */
	public static DBDataSource createDBDataSource(Database database) {
		return DBDataSource.create(database);
	}

	/**
	 * 识别JDBC驱动名
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param close
	 *            是否关闭
	 * @return 数据库类型
	 */
	public static DBType getDBType(Connection conn, boolean close) {
		DBType type = null;
		try {
			DatabaseMetaData meta = conn.getMetaData();
			type = getDBType(meta.getDatabaseProductName());
			if (type == null) {
				type = getDBType(meta.getDriverName());
			}
		} catch (SQLException e) {
			throw new RuntimeException("Identify driver error!", e);
		} finally {
			if (close) {
				IOUtil.close(conn);
			}
		}
		return type;
	}

	/**
	 * 获取数据库类型方法
	 */
	public static DBType getDBType(String info) {
		if (StringUtil.isEmpty(info)) {
			return null;
		}
		info = info.toLowerCase();
		for (DBType type : DBType.values()) {
			if (info.contains(type.getMatch().toLowerCase())) {
				return type;
			}
		}
		return null;
	}

	public static SqlValues toSqlValues(String sql, Map<String, Object> param) {

		List<String> names = new ArrayList<String>();
		Pattern pattern = Pattern.compile(":([\\w|\\-|.|:|$|#|+|_|-|{|}|\\]|\\[]+)");
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			String group = matcher.group();
			names.add(group.replace(":", ""));
			group = group.replaceAll("\\{", "\\\\{");
			group = group.replaceAll("\\}", "\\\\}");
			group = group.replaceAll("\\[", "\\\\[");
			group = group.replaceAll("\\]", "\\\\]");
			sql = sql.replaceFirst(group, "?");
		}
		List<Object> values = new ArrayList<Object>();
		if (names != null && param != null) {
			for (int i = 0; i < names.size(); i++) {
				String name = names.get(i);
				Object value = param.get(name);
				values.add(value);
			}
		}
		return new SqlValues(sql, values);
	}

	public static SqlValues toSqlValues(SqlParam sqlParam) {

		return toSqlValues(sqlParam.getSql(), sqlParam.getParam());
	}

	public static SqlValuesList toSqlValuesList(SqlParamList sqlParamList) {
		SqlValuesList sqlValuesList = new SqlValuesList();
		if (sqlParamList != null) {
			for (SqlParam sqlParam : sqlParamList) {
				SqlValues sqlValues = toSqlValues(sqlParam);
				sqlValuesList.add(sqlValues);
			}
		}
		return sqlValuesList;
	}

	public static void setPreparedStatementValues(PreparedStatement ps, List<Object> values) throws SQLException {
		if (ps == null || values == null) {
			return;
		}
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				Object value = values.get(i);
				int valueIndex = i + 1;
				if (null != value) {
					if (value instanceof java.util.Date) {
						// 日期特殊处理
						if (value instanceof java.sql.Date) {
							ps.setDate(valueIndex, (java.sql.Date) value);
						} else if (value instanceof java.sql.Time) {
							ps.setTime(valueIndex, (java.sql.Time) value);
						} else {
							Timestamp timestamp = new java.sql.Timestamp(((java.util.Date) value).getTime());
							ps.setTimestamp(valueIndex, timestamp);
						}
					} else if (value instanceof Number) {
						// 针对大数字类型的特殊处理
						if (value instanceof BigInteger) {
							// BigInteger转为Long
							ps.setLong(valueIndex, ((BigInteger) value).longValue());
						} else if (value instanceof BigDecimal) {
							// BigDecimal的转换交给JDBC驱动处理
							ps.setBigDecimal(valueIndex, (BigDecimal) value);
						} else {
							// 普通数字类型按照默认传入
							ps.setObject(valueIndex, value);
						}
					} else {
						ps.setObject(valueIndex, value);
					}
				} else {
					final ParameterMetaData pmd = ps.getParameterMetaData();
					int sqlType = Types.VARCHAR;
					try {
						sqlType = pmd.getParameterType(valueIndex);
					} catch (SQLException e) {
						// ignore
						// log.warn("Null param of index [{}] type get failed,
						// by:
						// {}",
						// paramIndex, e.getMessage());
					}
					ps.setNull(valueIndex, sqlType);
				}
			}
		}
	}

	public static List<Map<String, Object>> toList(ResultSet rs) throws SQLException {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		ResultSetMetaData rsmd = rs.getMetaData();// 获得列集
		int columnCount = rsmd.getColumnCount();
		String columnName = null;
		while (rs.next()) {
			Map<String, Object> value = new HashMap<String, Object>();
			for (int i = 0; i < columnCount; i++) {
				int sqlIndex = i + 1;
				columnName = rsmd.getColumnLabel(sqlIndex);
				if (StringUtil.isEmpty(columnName)) {
					columnName = rsmd.getColumnName(sqlIndex);
				}
				Object v = rs.getObject(sqlIndex);
				if (v != null && v instanceof Clob) {
					Clob clob = (Clob) v;
					try {
						v = IOUtil.read(clob.getAsciiStream());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				value.put(columnName, v);
			}
			result.add(value);
		}
		return result;

	}

	public static int batchExecute(Connection conn, List<String> sqls) throws SQLException {
		Statement statement = null;
		try {
			statement = conn.createStatement();
			for (String sql : sqls) {
				statement.addBatch(sql);
			}

			int[] res = statement.executeBatch();
			int count = 0;
			if (res != null) {
				for (int c : res) {
					count += c;
				}
			}
			return count;
		} finally {
			IOUtil.close(statement);
		}
	}

	public static int execute(Connection conn, String sql, List<Object> values, List<Object> keyValues)
			throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			if (keyValues == null) {
				ps = conn.prepareStatement(sql);
			} else {
				ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			}
			DBUtil.setPreparedStatementValues(ps, values);
			int res = ps.executeUpdate();
			if (keyValues != null) {
				// 获取自增列
				rs = ps.getGeneratedKeys();

				List<Map<String, Object>> list = DBUtil.toList(rs);
				if (list != null && list.size() > 0) {
					Map<String, Object> one = list.get(0);
					for (Object value : one.values()) {
						keyValues.add(value);
					}
				}
			}
			return res;
		} finally {
			IOUtil.close(rs, ps);
		}
	}

	public static int batchExecute(Connection conn, String sql, List<List<Object>> valuesList) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);

			if (valuesList != null) {
				for (List<Object> values : valuesList) {
					DBUtil.setPreparedStatementValues(ps, values);
					ps.addBatch();
				}
			}
			int[] res = ps.executeBatch();

			int count = 0;
			if (res != null) {
				for (int c : res) {
					count += c;
				}
			}
			return count;
		} finally {
			IOUtil.close(ps);
		}
	}

	public static int batchExecute(Connection conn, SqlValuesList sqlValuesList, List<List<Object>> keyValuesList)
			throws SQLException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = 0;
		try {
			if (sqlValuesList != null) {
				for (int index = 0; index < sqlValuesList.size(); index++) {
					SqlValues sqlValues = sqlValuesList.get(index);
					try {
						if (keyValuesList == null) {
							ps = conn.prepareStatement(sqlValues.getSql());
						} else {
							ps = conn.prepareStatement(sqlValues.getSql(), Statement.RETURN_GENERATED_KEYS);
						}
						DBUtil.setPreparedStatementValues(ps, sqlValues.getValues());
						result = result + ps.executeUpdate();

						if (keyValuesList != null) {
							// 获取自增列
							rs = ps.getGeneratedKeys();
							List<Map<String, Object>> list = DBUtil.toList(rs);
							List<Object> keyValues = new ArrayList<Object>();
							keyValuesList.add(keyValues);
							if (list != null && list.size() > 0) {
								Map<String, Object> one = list.get(0);
								for (Object value : one.values()) {
									keyValues.add(value);
								}
							}
						}

					} finally {
						IOUtil.close(rs, ps);
					}
				}
			}
		} finally {
			IOUtil.close(ps);
		}
		return result;
	}

}
