package com.teamide.ide.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.db.DataSourceFactory;
import com.teamide.db.TableUtil;
import com.teamide.db.bean.Database;
import com.teamide.factory.DatabaseFactory;
import com.teamide.service.IService;
import com.teamide.util.StringUtil;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.configure.IDEOptions;

public class IDEFactory {

	static final Map<String, ClientSession> TOKEN_SESSION_CACHE = new HashMap<String, ClientSession>();

	static final Map<String, Session> TOKEN_WEBSOCKET_SESSION_CACHE = new HashMap<String, Session>();

	public static ClientSession getClientSession(String token) {
		return TOKEN_SESSION_CACHE.get(token);
	}

	@SuppressWarnings("unchecked")
	public static Set<String> getClientTokens(ClientSession clientSession) {
		Set<String> tokens = new HashSet<String>();
		if (clientSession.get("tokens") != null) {
			tokens = (Set<String>) clientSession.get("tokens");
		}
		clientSession.set("tokens", tokens);
		return tokens;
	}

	public static void setClientSession(String token, ClientSession clientSession) {
		TOKEN_SESSION_CACHE.put(token, clientSession);

		Set<String> tokens = IDEFactory.getClientTokens(clientSession);
		tokens.add(token);
	}

	public static void removeClientSession(String token) {
		ClientSession clientSession = getClientSession(token);
		if (clientSession != null) {
			Set<String> tokens = IDEFactory.getClientTokens(clientSession);
			tokens.remove(token);
		}
		TOKEN_SESSION_CACHE.remove(token);

	}

	public static Session getWebsocketSession(String token) {
		return TOKEN_WEBSOCKET_SESSION_CACHE.get(token);
	}

	public static void setWebsocketSession(String token, Session session) {
		TOKEN_WEBSOCKET_SESSION_CACHE.put(token, session);
	}

	public static void removeWebsocketSession(String token) {
		TOKEN_WEBSOCKET_SESSION_CACHE.remove(token);
	}

	public static String getDefaultPassword() {

		IDEConfigure setting = IDEConfigure.get();
		String password = null;
		if (setting != null) {
			password = setting.getDefaultpassword();
		}
		if (StringUtil.isEmpty(password)) {
			password = "123456";
		}
		return password;
	}

	public static Database getDatabase() {

		return IDEOptions.get().jdbc;
	}

	public static final String getRealtablename(Class<?> clazz, Map<String, Object> data) throws Exception {

		return TableUtil.getRealtablename(clazz, data, getService().getDao().getDataSourceFactory());
	}

	public static DataSourceFactory getDSFactory() {

		try {
			if (getDatabase() != null) {
				return DataSourceFactory.create(getDatabase());
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static IService getService() {

		try {
			if (getDatabase() != null) {
				return DatabaseFactory.newService(getDatabase(), new JSONObject());
			}
		} catch (Exception e) {
		}
		return null;
	}

	// static Engine engine = null;
	//
	// static Object lock = new Object();
	//
	// public static Engine getEngine() {
	// if (engine != null) {
	// return engine;
	// }
	// synchronized (lock) {
	// if (engine != null) {
	// return engine;
	// }
	// try {
	// engine = new com.teamide.protect.ide.engine.Engine();
	// } catch (Exception e) {
	// }
	// return engine;
	// }
	// }

}
