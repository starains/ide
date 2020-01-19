package com.teamide.ide.protect.service;

import java.util.List;

import javax.annotation.Resource;

import com.teamide.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.RoleBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.service.ILoginService;

@Resource
public class LoginService implements ILoginService {

	@Override
	public UserBean doLogin(ClientSession session, String loginname, String password) throws Exception {

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
		if (session != null) {
			JSONObject userJSON = (JSONObject) JSONObject.toJSON(user);

			session.doLogin(userJSON.toJavaObject(com.teamide.bean.UserBean.class));

			List<RoleBean> roles = new RoleService().queryUserVisibleRoles(user.getId());
			session.setCache("user", user);
			session.setCache("roles", roles);
			session.setCache("isManager", false);
			for (RoleBean role : roles) {
				if (role.isForsuper()) {
					session.setCache("isManager", true);
				}
			}

		}
		return user;
	}

}
