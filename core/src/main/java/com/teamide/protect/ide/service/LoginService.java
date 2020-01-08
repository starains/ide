package com.teamide.protect.ide.service;

import java.util.List;

import javax.annotation.Resource;

import com.teamide.util.StringUtil;
import com.teamide.ide.bean.RoleBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.service.ILoginService;

@Resource
public class LoginService implements ILoginService {

	@Override
	public UserBean doLogin(Client client, String loginname, String password) throws Exception {

		if (StringUtil.isEmpty(loginname)) {
			throw new Exception("登录名称不能为空.");

		}
		if (StringUtil.isEmpty(password)) {
			throw new Exception("密码不能为空.");

		}
		UserBean user = new UserService().getByLogin(loginname, password);

		if (user == null) {
			throw new Exception("用户名或密码错误！");
		}
		if (client != null) {
			client.doLogin(user);

			List<RoleBean> roles = new RoleService().queryUserVisibleRoles(user.getId());
			client.setRoles(roles);

		}
		return user;
	}

}
