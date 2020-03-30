package com.teamide.db.dialect.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.teamide.db.DBUtil;
import com.teamide.db.bean.ColumnIndex;
import com.teamide.db.dialect.Wrapper;
import com.teamide.param.PageSqlParam;
import com.teamide.util.IOUtil;

/**
 * MySQL方言
 * 
 *
 */
public class MysqlDialect extends AnsiSqlDialect {

	public MysqlDialect() {

		wrapper = new Wrapper('`');
	}

	@Override
	public String wrapPageSql(String sql, PageSqlParam page) {
		StringBuffer pageSql = new StringBuffer(sql);

		pageSql.append(" LIMIT ").append(getStartPosition(page)).append(", ").append(page.getPagesize());
		return pageSql.toString();
	}

	public String sqlForCreateDatabase(String name) throws Exception {

		StringBuffer sql = new StringBuffer();
		sql.append(super.sqlForCreateDatabase(name));
		sql.append(" DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ");
		return sql.toString();
	}

	@Override
	public String getTableComment(Connection conn, String tableName) throws SQLException {
		String sql = "SELECT TABLE_NAME,TABLE_COMMENT ";

		sql += " FROM INFORMATION_SCHEMA.TABLES  ";

		sql += " WHERE TABLE_SCHEMA = '" + conn.getCatalog() + "' ";
		sql += " AND TABLE_NAME = '" + tableName + "' ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			List<Map<String, Object>> result = DBUtil.toList(rs);
			if (result.size() > 0) {
				Map<String, Object> one = result.get(0);
				if (one.get("TABLE_COMMENT") != null) {
					return String.valueOf(one.get("TABLE_COMMENT"));
				}
			}
		} finally {
			IOUtil.close(rs, ps);
		}
		return null;
	}

	@Override
	public List<ColumnIndex> getColumnIndexs(Connection conn, String tableName) throws SQLException {
		String sql = "SELECT KCU.TABLE_NAME,KCU.COLUMN_NAME,TC.CONSTRAINT_TYPE,TC.CONSTRAINT_NAME ";

		sql += " FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE KCU ";
		sql += " LEFT JOIN  INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC ";
		sql += " ON KCU.TABLE_SCHEMA = TC.TABLE_SCHEMA AND KCU.TABLE_NAME = TC.TABLE_NAME ";
		sql += " AND KCU.CONSTRAINT_NAME = TC.CONSTRAINT_NAME ";

		sql += " WHERE KCU.TABLE_SCHEMA = '" + conn.getCatalog() + "' ";
		sql += " AND KCU.TABLE_NAME = '" + tableName + "' ";
		sql += " AND KCU.CONSTRAINT_NAME != 'PRIMARY' ";
		List<ColumnIndex> indexs = new ArrayList<ColumnIndex>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			List<Map<String, Object>> result = DBUtil.toList(rs);
			for (Map<String, Object> one : result) {
				ColumnIndex index = new ColumnIndex();
				if (one.get("COLUMN_NAME") != null) {
					index.setColumn(String.valueOf(one.get("COLUMN_NAME")));
				}
				if (one.get("CONSTRAINT_NAME") != null) {
					index.setName(String.valueOf(one.get("CONSTRAINT_NAME")));
				}
				if (one.get("CONSTRAINT_TYPE") != null) {
					String CONSTRAINT_TYPE = String.valueOf(one.get("CONSTRAINT_TYPE"));
					if ("UNIQUE".equalsIgnoreCase(CONSTRAINT_TYPE)) {
						index.setNonunique(false);
					}
					index.setType(String.valueOf(one.get("CONSTRAINT_TYPE")));
				}
				indexs.add(index);
			}
		} finally {
			IOUtil.close(rs, ps);
		}
		return indexs;
	}
}
