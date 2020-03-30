package com.teamide.db.dialect;

import com.teamide.db.dialect.impl.H2Dialect;
import com.teamide.db.dialect.impl.MysqlDialect;
import com.teamide.db.dialect.impl.OracleDialect;
import com.teamide.db.dialect.impl.PostgresqlDialect;
import com.teamide.db.dialect.impl.SqlServer2012Dialect;
import com.teamide.db.dialect.impl.Sqlite3Dialect;

public enum DialectType {
	/** JDBC 驱动 MySQL */
	MYSQL("MYSQL", "com.mysql.jdbc.Driver", MysqlDialect.class),
	/** JDBC 驱动 MySQL，在6.X版本中变动驱动类名，且使用SPI机制 */
	MYSQL_V6("MYSQL_V6", "com.mysql.cj.jdbc.Driver", MysqlDialect.class),

	/** JDBC 驱动 Oracle */
	ORACLE("ORACLE", "oracle.jdbc.OracleDriver", OracleDialect.class),
	/** JDBC 驱动 Oracle，旧版使用 */
	ORACLE_OLD("ORACLE_OLD ", "oracle.jdbc.driver.OracleDriver", OracleDialect.class),

	/** JDBC 驱动 PostgreSQL */
	POSTGRESQL("POSTGRESQL", "org.postgresql.Driver", PostgresqlDialect.class),

	/** JDBC 驱动 SQLLite3 */
	SQLLITE3("SQLLITE3", "org.sqlite.JDBC", Sqlite3Dialect.class),

	/** JDBC 驱动 SQLServer */
	SQLSERVER("SQLSERVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver", SqlServer2012Dialect.class),

	/** JDBC 驱动 Hive */
	HIVE("HIVE", "org.apache.hadoop.hive.jdbc.HiveDriver", null),
	/** JDBC 驱动 Hive2 */
	HIVE2("HIVE2", "org.apache.hive.jdbc.HiveDriver", null),

	/** JDBC 驱动 H2 */
	H2("H2", "org.h2.Driver", H2Dialect.class),

	/** JDBC 驱动 Derby */
	DERBY("DERBY", "org.apache.derby.jdbc.ClientDriver", null),
	/** JDBC 驱动 Derby嵌入式 */
	DERBY_EMBEDDED("DERBY_EMBEDDED", "org.apache.derby.jdbc.EmbeddedDriver", null);

	private DialectType(String name, String drive, Class<?> dialectClass) {

		this.name = name;
		this.drive = drive;
		this.dialectClass = dialectClass;
	}

	private final String name;

	private final String drive;

	private final Class<?> dialectClass;

	public String getName() {

		return name;
	}

	public String getDrive() {

		return drive;
	}

	public Class<?> getDialectClass() {

		return dialectClass;
	}

}
