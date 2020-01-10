package com.teamide.ide.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.teamide.client.ClientSession;
import com.teamide.ide.factory.IDEFactory;

@ServerEndpoint(value = "/websocket/{token}")
public class WebSocketServer {

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(@PathParam("token") String token, Session session) {
		ClientSession clientSession = IDEFactory.getClientSession(token);
		if (clientSession == null) {
			try {
				session.getBasicRemote().sendText("-1");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			IDEFactory.setWebsocketSession(token, session);
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(@PathParam("token") String token, Session session) {
		IDEFactory.removeClientSession(token);
		IDEFactory.removeWebsocketSession(token);
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
	}

	/**
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
	}

}