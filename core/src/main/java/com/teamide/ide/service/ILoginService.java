package com.teamide.ide.service;

import com.teamide.ide.bean.UserBean;
import com.teamide.ide.client.Client;

public interface ILoginService {

	public UserBean doLogin(Client client, String loginname, String password) throws Exception;
}
