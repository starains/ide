package com.teamide.ide.controller.handler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.bean.Status;
import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.UserLoginBean;
import com.teamide.ide.listen.IDEListen;
import com.teamide.ide.listen.IDEListenHandler;
import com.teamide.ide.service.impl.BaseService;
import com.teamide.ide.service.impl.UserLoginService;
import com.teamide.util.ResponseUtil;

public class ListenHandler {

	public void listen(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ClientSession session = ClientHandler.getSession(request);
		if (session.get("USER_LOGIN_ID") != null) {
			String USER_LOGIN_ID = String.valueOf(session.get("USER_LOGIN_ID"));
			UserLoginBean loginBean = new UserLoginBean();
			loginBean.setId(USER_LOGIN_ID);
			loginBean.setEndtime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));
			new UserLoginService().update(session, loginBean);
		}

		IDEListen ideListen = IDEListenHandler.get(session);
		Status status = Status.SUCCESS();
		try {
			status.setValue(ideListen.waitListen());
			ResponseUtil.outJSON(response, status);
		} catch (Exception e) {
		}

	}

}
