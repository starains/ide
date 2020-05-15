package com.teamide.ide.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import com.teamide.client.ClientSession;
import com.teamide.db.TableUtil;
import com.teamide.service.IService;
import com.teamide.service.impl.Service;
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

		IDEConfigure configure = IDEConfigure.get();
		String password = null;
		if (configure != null && configure.getAccount() != null) {
			password = configure.getAccount().getDefaultpassword();
		}
		if (StringUtil.isEmpty(password)) {
			password = "123456";
		}
		return password;
	}

	public static final String getRealtablename(Class<?> clazz, Map<String, Object> data) throws Exception {

		return TableUtil.getRealtablename(clazz, data, getService().getDBDataSource());
	}

	public static IService getService() {

		try {
			if (IDEOptions.get().getJdbc() != null) {
				return new Service(DataSourceFactory.getDBDataSource(IDEOptions.get().getJdbc()));
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
