package com.teamide.ide.protect.engine;
//package com.teamide.protect.ide.engine;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpSession;
//import javax.websocket.Session;
//
//@Resource
//public class Engine implements com.teamide.ide.engine.Engine {
//	// 连接超时
//	public static final long MAX_TIME_OUT = 2 * 60 * 1000;
//
//	// 最大消息长度
//	public static final int MAX_MESSAGE_SIZE = 1024 * 1024 * 20;
//
//	@Override
//	public EngineSession open(HttpSession httpSession, Session websocketSession, String token) {
//		websocketSession.setMaxIdleTimeout(MAX_TIME_OUT);
//		websocketSession.setMaxBinaryMessageBufferSize(MAX_MESSAGE_SIZE);
//		websocketSession.setMaxTextMessageBufferSize(MAX_MESSAGE_SIZE);
//		EngineSession session = new EngineSession(httpSession, websocketSession, token);
//		session.onOpen();
//		return session;
//	}
//
//}
