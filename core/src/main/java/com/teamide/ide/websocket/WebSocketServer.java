//package com.teamide.ide.websocket;
//
//import java.io.EOFException;
//
//import javax.servlet.http.HttpSession;
//import javax.websocket.EndpointConfig;
//import javax.websocket.OnClose;
//import javax.websocket.OnError;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//
//import com.teamide.ide.engine.EngineSession;
//import com.teamide.ide.factory.IDEFactory;
//
//@ServerEndpoint(value = "/websocket/{token}", configurator = HttpSessionConfigurator.class)
//public class WebSocketServer {
//
//	EngineSession engineSession;
//
//	/**
//	 * 连接建立成功调用的方法
//	 */
//	@OnOpen
//	public void onOpen(Session session, @PathParam("token") String token, EndpointConfig config) {
//		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
//		engineSession = IDEFactory.getEngine().open(httpSession, session, token);
//	}
//
//	/**
//	 * 连接关闭调用的方法
//	 */
//	@OnClose
//	public void onClose() {
//		if (engineSession != null) {
//			engineSession.onClose();
//		}
//	}
//
//	/**
//	 * 收到客户端消息后调用的方法
//	 *
//	 * @param message
//	 *            客户端发送过来的消息
//	 */
//	@OnMessage
//	public void onMessage(String message, Session session) {
//		if (engineSession != null) {
//			engineSession.onMessage(message);
//		}
//	}
//
//	/**
//	 * 
//	 * @param session
//	 * @param error
//	 */
//	@OnError
//	public void onError(Session session, Throwable error) {
//		if (error instanceof EOFException) {
//
//		} else {
//			error.printStackTrace();
//		}
//		onClose();
//	}
//
//}