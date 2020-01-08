package com.teamide.ide.engine;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

public interface Engine {

	public EngineSession open(HttpSession httpSession, Session websocketSession, String token);
}
