package com.teamide.db.dialect.impl;

import com.teamide.db.dialect.Wrapper;
import com.teamide.param.PageSqlParam;
import com.teamide.util.StringUtil;

/**
 * SQLServer2012 方言
 */
public class SqlServer2012Dialect extends AnsiSqlDialect {

	public SqlServer2012Dialect() {

		// 双引号和中括号适用，双引号更广泛
		wrapper = new Wrapper('"');
	}

	@Override
	public String wrapPageSql(String sql, PageSqlParam page) {
		StringBuffer pageSql = new StringBuffer(sql);

		if (false == StringUtil.containsIgnoreCase(sql.toString(), "ORDER BY")) {
			// offset 分页必须要跟在order by后面，没有情况下补充默认排序
			pageSql.append(" ORDER BY CURRENT_TIMESTAMP");
		}
		pageSql.append(" OFFSET ").append(getStartPosition(page))//
				.append(" ROW FETCH NEXT ")// row和rows同义词
				.append(page.getPagesize())//
				.append(" ROW ONLY");//
		return pageSql.toString();
	}

}
