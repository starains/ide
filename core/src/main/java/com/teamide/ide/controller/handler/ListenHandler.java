package com.teamide.ide.controller.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.bean.Status;
import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.ide.listen.IDEListen;
import com.teamide.ide.listen.IDEListenHandler;

public class ListenHandler {

	public void listen(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ClientSession session = ClientHandler.getSession(request);
		IDEListen ideListen = IDEListenHandler.get(session);
		Status status = Status.SUCCESS();
		status.setValue(ideListen.waitListen());

	}

}
