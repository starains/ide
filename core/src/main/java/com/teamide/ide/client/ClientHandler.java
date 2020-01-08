package com.teamide.ide.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ClientHandler {

	public static final String CLIENT_SESSION_KEY = "IDE_CLIENT_SESSION_KEY";

	public static Client get(HttpServletRequest request) {

		return get(request.getSession());
	}

	public static Client get(HttpSession session) {

		Client client = null;
		if (session == null) {
			return null;
		}
		if (session.getAttribute(CLIENT_SESSION_KEY) != null) {
			client = (Client) session.getAttribute(CLIENT_SESSION_KEY);
		}
		if (client == null) {
			client = new Client(session);
			session.setAttribute(CLIENT_SESSION_KEY, client);
		}
		return client;
	}
}
