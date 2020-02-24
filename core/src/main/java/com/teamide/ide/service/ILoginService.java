package com.teamide.ide.service;

import com.teamide.client.ClientSession;
import com.teamide.ide.bean.UserBean;

public interface ILoginService {

	public UserBean doLogin(ClientSession session, String loginname, String password) throws Exception;

	public UserBean doLoginById(ClientSession session, String id) throws Exception;
}
