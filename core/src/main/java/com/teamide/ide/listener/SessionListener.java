package com.teamide.ide.listener;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.ide.factory.IDEFactory;

@WebListener
public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent event) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		if (event.getSession() != null) {
			ClientSession clientSession = ClientHandler.getSession(event.getSession());
			Set<?> tokens = IDEFactory.getClientTokens(clientSession);
			Set<String> ts = new HashSet<String>();
			for (Object token : tokens) {
				ts.add(String.valueOf(token));
			}
			for (Object token : ts) {
				IDEFactory.removeClientSession(String.valueOf(token));
				IDEFactory.removeWebsocketSession(String.valueOf(token));
			}
		}

	}

}
