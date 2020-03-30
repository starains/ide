package com.teamide.db.dialect.impl;

import com.teamide.db.dialect.Wrapper;
import com.teamide.param.PageSqlParam;

/**
 * H2方言
 * 
 *
 */
public class H2Dialect extends AnsiSqlDialect {

	public H2Dialect() {

		wrapper = new Wrapper('"', '"');
	}

	@Override
	public String wrapPageSql(String sql, PageSqlParam page) {
		StringBuffer pageSql = new StringBuffer(sql);
		// limit A , B 表示：A就是查询的起点位置，B就是你需要多少行。
		pageSql.append(" LIMIT ").append(getStartPosition(page)).append(" , ").append(page.getPagesize());
		return pageSql.toString();
	}
}
