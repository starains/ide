package com.teamide.db.dialect.impl;

import com.teamide.db.dialect.Wrapper;

/**
 * SqlLite3方言
 */
public class Sqlite3Dialect extends AnsiSqlDialect {

	public Sqlite3Dialect() {

		wrapper = new Wrapper('[', ']');
	}
}
