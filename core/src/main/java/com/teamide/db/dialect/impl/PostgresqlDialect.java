package com.teamide.db.dialect.impl;

import com.teamide.db.dialect.Wrapper;

/**
 * Postgree方言
 *
 */
public class PostgresqlDialect extends AnsiSqlDialect {

	public PostgresqlDialect() {

		wrapper = new Wrapper('"');
	}

}
