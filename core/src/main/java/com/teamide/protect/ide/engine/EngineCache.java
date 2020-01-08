package com.teamide.protect.ide.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EngineCache {

	private static final Set<EngineSession> SESSIONS = new HashSet<EngineSession>();

	private static final Map<String, EngineSession> TOKEN_SESSION = new HashMap<String, EngineSession>();

	private static final Map<String, Set<EngineSession>> SESSIONID_SESSIONS = new HashMap<String, Set<EngineSession>>();

	public static Set<EngineSession> getSessions() {
		return SESSIONS;
	}

	public static void add(EngineSession session) {
		if (session == null || session.token == null || session.httpSession == null) {
			return;
		}
		SESSIONS.add(session);
		TOKEN_SESSION.put(session.token, session);
		Set<EngineSession> sessions = SESSIONID_SESSIONS.get(session.httpSession.getId());
		if (sessions == null) {
			sessions = new HashSet<EngineSession>();
		}
		sessions.add(session);
		SESSIONID_SESSIONS.put(session.httpSession.getId(), sessions);
	}

	public static void remove(EngineSession session) {
		if (session == null || session.token == null || session.httpSession == null) {
			return;
		}
		SESSIONS.remove(session);
		TOKEN_SESSION.remove(session.token);
		Set<EngineSession> sessions = SESSIONID_SESSIONS.get(session.httpSession.getId());
		if (sessions == null) {
			sessions = new HashSet<EngineSession>();
		}
		sessions.remove(session);
		SESSIONID_SESSIONS.put(session.httpSession.getId(), sessions);
	}

	public static Set<EngineSession> getBySessionid(String sessionid) {
		Set<EngineSession> sessions = SESSIONID_SESSIONS.get(sessionid);
		if (sessions == null) {
			sessions = new HashSet<EngineSession>();
		}
		SESSIONID_SESSIONS.put(sessionid, sessions);
		return sessions;
	}
}
