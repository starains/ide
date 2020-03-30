//package com.teamide.db.holder;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.teamide.dao.ITransactionConnection;
//
//public class TransactionConnectionHolder {
//
//	// 本地线程共享对象
//	private static final ThreadLocal<Map<String, ITransactionConnection>> THREAD_LOCAL = new ThreadLocal<Map<String, ITransactionConnection>>();
//
//	public synchronized static void init() {
//		if (THREAD_LOCAL.get() == null) {
//			THREAD_LOCAL.set(new HashMap<String, ITransactionConnection>());
//		}
//	}
//
//	public static ITransactionConnection get(String key) {
//		init();
//		ITransactionConnection transactionConnection = THREAD_LOCAL.get().get(key);
//		return transactionConnection;
//	}
//
//	public static ITransactionConnection remove(String key) {
//		init();
//		return THREAD_LOCAL.get().remove(key);
//	}
//
//	public static void put(ITransactionConnection transactionConnection) {
//		init();
//		if (transactionConnection != null && transactionConnection.getKey() != null) {
//			THREAD_LOCAL.get().put(transactionConnection.getKey(), transactionConnection);
//		}
//	}
//
//	public static void remove() {
//		THREAD_LOCAL.remove();
//	}
//}
