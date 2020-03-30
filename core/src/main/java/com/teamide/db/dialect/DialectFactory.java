package com.teamide.db.dialect;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.teamide.db.DBUtil;
import com.teamide.db.dialect.impl.AnsiSqlDialect;
import com.teamide.db.enums.DBType;
import com.teamide.util.StringUtil;

/**
 * 方言工厂类
 */
public class DialectFactory {

	private DialectFactory() {

	}

	public static Dialect newDefaultDialect() {

		return new AnsiSqlDialect();
	}

	/**
	 * 根据驱动名创建方言<br>
	 * 驱动名是不分区大小写完全匹配的
	 * 
	 * @param type
	 *            DialectType驱动类型
	 * @return 方言
	 */
	public static Dialect newDialect(DialectType type) {

		Class<?> dialectClass = null;
		if (type != null) {
			dialectClass = type.getDialectClass();
		}
		Dialect dialect = null;
		if (dialectClass == null) {
			dialect = newDefaultDialect();
		} else {
			try {

				dialect = (Dialect) dialectClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return dialect;
	}

	/**
	 * 根据驱动名创建方言<br>
	 * 驱动名是不分区大小写完全匹配的
	 * 
	 * @param driverName
	 *            JDBC驱动类名
	 * @return 方言
	 */
	public static Dialect newDialect(String driverName) {

		DialectType type = null;
		if (!StringUtil.isEmpty(driverName)) {
			for (DialectType t : DialectType.values()) {
				if (driverName.equalsIgnoreCase(t.getDrive())) {
					type = t;
					break;
				}
			}
			if (type == null) {
				for (DialectType t : DialectType.values()) {
					if (driverName.equalsIgnoreCase(t.getName())) {
						type = t;
						break;
					}
				}
			}
		}

		return newDialect(type);
	}

	/**
	 * 通过JDBC URL等信息识别JDBC驱动名
	 * 
	 * @param nameContainsProductInfo
	 *            包含数据库标识的字符串
	 * @return 驱动
	 */
	public static DialectType identifyDriver(String nameContainsProductInfo) {

		if (StringUtil.isEmpty(nameContainsProductInfo)) {
			return null;
		}
		// 全部转为小写，忽略大小写
		nameContainsProductInfo = nameContainsProductInfo.toLowerCase();

		DialectType type = null;
		DBType dbType = DBUtil.getDBType(nameContainsProductInfo);
		if (dbType != null) {
			type = dbType.getDialectType();
		}

		return type;
	}

	/**
	 * 创建方言
	 * 
	 * @param ds
	 *            数据源
	 * @return 方言
	 */
	public static Dialect newDialect(DataSource ds) throws SQLException {
		if (ds == null) {
			return null;
		}

		DBType dbType = DBUtil.getDBType(ds.getConnection(), true);
		DialectType type = null;
		if (dbType != null) {
			type = dbType.getDialectType();
		}
		return newDialect(type);
	}

}
