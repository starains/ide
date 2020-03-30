package com.teamide.db.holder;

import com.teamide.db.DBDataSource;

public class DBDataSourceHolder {

	// 本地线程共享对象
	private static final ThreadLocal<DBDataSource> THREAD_LOCAL = new ThreadLocal<DBDataSource>();

	public static DBDataSource get() {
		return THREAD_LOCAL.get();
	}

	public static void set(DBDataSource dbDataSource) {
		THREAD_LOCAL.set(dbDataSource);
	}

	public static void remove() {
		THREAD_LOCAL.remove();
	}
}
