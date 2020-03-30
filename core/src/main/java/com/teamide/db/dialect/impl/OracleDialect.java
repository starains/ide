package com.teamide.db.dialect.impl;

import com.teamide.db.dialect.Wrapper;
import com.teamide.param.PageSqlParam;

/**
 * Oracle 方言
 *
 */
public class OracleDialect extends AnsiSqlDialect {

	public OracleDialect() {

		wrapper = new Wrapper('"'); // Oracle所有字段名用双引号包围，防止字段名或表名与系统关键字冲突
	}

	@Override
	public String wrapPageSql(String sql, PageSqlParam page) {
		StringBuffer pageSql = new StringBuffer();

		final int[] startEnd = getStartEnd(page);
		pageSql.setLength(0);
		pageSql.append("SELECT * FROM ( SELECT row_.*, ROWNUM ROWNUM_ from ( ");
		pageSql.append(sql);
		pageSql.append(" ) ROW_ WHERE ROWNUM <= ").append(startEnd[1])//
				.append(") TABLE_ALIAS")//
				.append(" where TABLE_ALIAS.ROWNUM_ >= ").append(startEnd[0]);//
		return pageSql.toString();

	}

	public String getCreateTableColumntComment(String comment) {

		return "";
	}

	public String getCreateTableComment(String comment) {

		return "";
	}

	public String sqlForSelectPrimaryKey(String table, String catalog, String schema) {

		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT B.COLUMN_NAME as COLUMN_NAME ");
		sql.append(" FROM USER_CONSTRAINTS A, USER_IND_COLUMNS B ");
		sql.append(" WHERE A.INDEX_NAME = B.INDEX_NAME  ");
		sql.append(" AND A.INDEX_OWNER = '" + schema + "' ");
		sql.append(" AND B.TABLE_NAME = '" + table + "' ");
		sql.append(" AND A.CONSTRAINT_TYPE = 'P' ");

		return sql.toString();
	}

	public String getAddColumn() {

		return "ADD";
	}
}
