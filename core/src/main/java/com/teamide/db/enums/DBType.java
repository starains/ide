package com.teamide.db.enums;

import com.teamide.db.dialect.DialectType;
import com.teamide.util.StringUtil;

/**
 * 数据库类型
 * 
 * @author ZhuLiang
 *
 */
public enum DBType {

	MYSQL("MYSQL", "mysql", DialectType.MYSQL),

	ORACLE("ORACLE", "oracle", DialectType.ORACLE),

	POSTGRESQL("POSTGRESQL", "postgresql", DialectType.POSTGRESQL),

	SQLITE("SQLITE", "sqlite", DialectType.SQLLITE3),

	SQLSERVER("SQLSERVER", "sqlserver", DialectType.SQLSERVER),

	HIVE("HIVE", "hive", DialectType.HIVE),

	H2("H2", "h2", DialectType.H2),

	DERBY("DERBY", "jdbc:derby://", DialectType.DERBY),

	DERBY_EMBEDDED("DERBY_EMBEDDED", "derby", DialectType.DERBY_EMBEDDED),

	;

	public static DBType get(String value) {
		DBType type = null;
		for (DBType one : DBType.values()) {
			if (StringUtil.isEmpty(one.getValue()) && StringUtil.isEmpty(value)) {
				type = one;
				break;
			}
			if (value != null && value.equalsIgnoreCase(one.getValue())) {
				type = one;
				break;
			}
		}
		return type;
	}

	private DBType(String value, String match, DialectType dialectType) {

		this.value = value;
		this.match = match;
		this.dialectType = dialectType;
	}

	private final String value;
	private final String match;
	private final DialectType dialectType;

	public String getValue() {

		return value;
	}

	public String getMatch() {
		return match;
	}

	public DialectType getDialectType() {
		return dialectType;
	}

}
