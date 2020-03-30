package com.teamide.db;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.teamide.db.bean.Database;
import com.teamide.db.dialect.Dialect;
import com.teamide.db.dialect.DialectFactory;

public class DBDataSource {
	static final String DBCP2 = "org.apache.commons.dbcp2.BasicDataSource";

	static final String C3P0 = "com.mchange.v2.c3p0.ComboPooledDataSource";

	static final String DRUID = "com.alibaba.druid.pool.DruidDataSource";

	private final Database database;

	private final DataSource dataSource;

	private final Dialect dialect;

	public DBDataSource(DataSource dataSource, Database database, Dialect dialect) {
		this.database = database;
		this.dataSource = dataSource;
		this.dialect = dialect;
	}

	public void destroy() {

		try {
			if (dataSource != null) {
				if (dataSource instanceof AutoCloseable) {
					((AutoCloseable) dataSource).close();
				} else {
					if (dataSource instanceof com.mchange.v2.c3p0.ComboPooledDataSource) {
						((com.mchange.v2.c3p0.ComboPooledDataSource) dataSource).close();
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public Database getDatabase() {
		return database;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public Dialect getDialect() {
		return dialect;
	}

	public static DBDataSource create(Database database) {
		if (database == null) {
			return null;
		}
		DataSource dataSource = null;
		try {
			Class.forName(DBCP2);
			dataSource = createDBCP2(database);
		} catch (ClassNotFoundException e) {
			try {
				Class.forName(C3P0);
				dataSource = createC3P0(database);
			} catch (ClassNotFoundException e1) {
				try {
					Class.forName(DRUID);
					dataSource = createDRUID(database);
				} catch (ClassNotFoundException e2) {
					throw new RuntimeException("C3P0 DBCP2 DRUID all does not exist.");
				}
			}
		}

		//
		Dialect dialect = DialectFactory.newDialect(database.getDriver());

		DBDataSource dbDataSource = new DBDataSource(dataSource, database, dialect);
		String key = getKey(database);
		DF_CACHE.put(key, dbDataSource);
		return dbDataSource;

	}

	private final static Map<String, DBDataSource> DF_CACHE = new HashMap<String, DBDataSource>();

	private static String getKey(Database database) {

		String key = "DB-DATASOURCE-";
		if (database != null) {
			key += database.getUrl();
			key += "-" + database.getDriver();
			key += "-" + database.getUsername();
			key += "-" + database.getPassword();
		}
		return key;
	}

	// public static final void destroy(Database database) {
	// String key = getKey(database);
	// DBDataSource dbDataSource = DF_CACHE.get(key);
	// if (dbDataSource != null) {
	// DF_CACHE.remove(key);
	// dbDataSource.destroy();
	// }
	// }

	public static DataSource createDBCP2(Database database) {
		if (database == null) {
			return null;
		}
		org.apache.commons.dbcp2.BasicDataSource ds = new org.apache.commons.dbcp2.BasicDataSource();

		// 基本信息
		ds.setUrl(database.getUrl());
		ds.setUsername(database.getUsername());
		ds.setPassword(database.getPassword());

		// 初始化连接
		if (database.getInitialsize() != null) {
			ds.setInitialSize(database.getInitialsize());
		} else {
			ds.setInitialSize(5);
		}
		if (database.getMaxtotal() != null) {
			// 设置最大连接数
			ds.setMaxTotal(database.getMaxtotal());
		} else {
			ds.setMaxTotal(10);
		}
		// 设置最小空闲连接

		if (database.getMinidle() != null) {
			ds.setMinIdle(database.getMinidle());
		}
		// 设置最大空闲连接
		if (database.getMaxidle() != null) {
			ds.setMaxIdle(database.getMaxidle());
		}
		// 设置最大等待时间
		if (database.getMaxwaitmillis() != null) {
			ds.setMaxWaitMillis(database.getMaxwaitmillis());
		} else {
			ds.setMaxWaitMillis(2000);
		}
		ds.setTestWhileIdle(true);
		ds.setTestOnReturn(true);
		ds.setTestOnBorrow(true);
		// 严重链接

		if (database.getValidationquery() != null) {
			ds.setValidationQuery(database.getValidationquery());
		}

		final String driver = database.getDriver();

		ds.setDriverClassName(driver);

		return ds;
	}

	public static DataSource createC3P0(Database database) {
		if (database == null) {
			return null;
		}
		com.mchange.v2.c3p0.ComboPooledDataSource ds = new com.mchange.v2.c3p0.ComboPooledDataSource();

		// 基本信息
		ds.setJdbcUrl(database.getUrl());
		ds.setUser(database.getUsername());
		ds.setPassword(database.getPassword());

		// 初始化连接
		if (database.getInitialsize() != null) {
			ds.setInitialPoolSize(database.getInitialsize());
		} else {
			ds.setInitialPoolSize(5);
		}
		if (database.getMaxtotal() != null) {
			// 设置最大连接数
			ds.setMaxPoolSize(database.getMaxtotal());
		} else {
			ds.setMaxPoolSize(10);
		}
		// 设置最小空闲连接

		if (database.getMinidle() != null) {
			ds.setMinPoolSize(database.getMinidle());
		}

		final String driver = database.getDriver();
		try {

			ds.setDriverClass(driver);
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}

		return ds;
	}

	public static DataSource createDRUID(Database database) {
		if (database == null) {
			return null;
		}
		com.alibaba.druid.pool.DruidDataSource ds = new com.alibaba.druid.pool.DruidDataSource();

		// 基本信息
		ds.setUrl(database.getUrl());
		ds.setUsername(database.getUsername());
		ds.setPassword(database.getPassword());

		// 初始化连接
		if (database.getInitialsize() != null) {
			ds.setInitialSize(database.getInitialsize());
		} else {
			ds.setInitialSize(5);
		}
		if (database.getMaxtotal() != null) {
			// 设置最大连接数
			ds.setMaxActive(database.getMaxtotal());
		} else {
			ds.setMaxActive(10);
		}
		// 设置最小空闲连接

		if (database.getMinidle() != null) {
			ds.setMinIdle(database.getMinidle());
		}
		// 设置最大空闲连接
		if (database.getMaxidle() != null) {
			// ds.setMaxIdle(database.getMaxidle());
		}
		// 设置最大等待时间
		if (database.getMaxwaitmillis() != null) {
			ds.setMaxWait(database.getMaxwaitmillis());
		} else {
			ds.setMaxWait(2000);
		}
		ds.setTestWhileIdle(true);
		ds.setTestOnReturn(true);
		ds.setTestOnBorrow(true);
		// 严重链接

		if (database.getValidationquery() != null) {
			ds.setValidationQuery(database.getValidationquery());
		}

		final String driver = database.getDriver();

		ds.setDriverClassName(driver);

		return ds;
	}
}
