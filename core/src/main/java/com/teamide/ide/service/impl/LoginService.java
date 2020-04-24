package com.teamide.ide.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.RoleBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.enums.UserActiveStatus;
import com.teamide.ide.enums.UserStatus;
import com.teamide.ide.service.ILoginService;
import com.teamide.ide.util.TokenUtil;

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

		return doLoginById(session, user.getId());
	}

	@Override
	public UserBean doLoginById(ClientSession session, String id) throws Exception {
		UserBean user = new UserService().get(id);
		if (user != null && session != null) {
			checkLogin(id);
			JSONObject userJSON = (JSONObject) JSONObject.toJSON(user);

			session.doLogin(userJSON.toJavaObject(com.teamide.bean.UserBean.class));

			List<RoleBean> roles = new RoleService().queryUserVisibleRoles(user.getId());
			session.setCache("user", user);
			session.setCache("roles", roles);
			session.setCache("isManager", false);
			JSONObject json = new JSONObject();
			json.put("id", user.getId());
			json.put("loginname", user.getLoginname());
			json.put("name", user.getName());
			json.put("timestamp", System.currentTimeMillis());
			session.setCache("LOGIN_USER_TOKEN", TokenUtil.getToken(json));
			for (RoleBean role : roles) {
				if (role.isForsuper()) {
					session.setCache("isManager", true);
				}
			}

		}
		return user;
	}

	public void checkLogin(String id) throws Exception {
		UserBean user = new UserService().get(id);
		if (user == null) {
			return;
		}
		IDEConfigure configure = IDEConfigure.get();
		UserStatus status = UserStatus.get(user.getStatus());
		switch (status) {
		case DESTROY:
			throw new Exception("账号已销毁，请联系管理员！");
		case DISABLE:
			throw new Exception("账号已禁用，请联系管理员！");
		case LOCK:
			Integer accountunlockminute = configure.getAccountunlockminute();

			if (accountunlockminute == null || accountunlockminute <= 0) {
				throw new Exception("账号已锁定，，请联系管理员解锁！");
			}

			String locktime = user.getLocktime();
			if (StringUtil.isEmpty(locktime)) {
				locktime = BaseService.PURE_DATETIME_FORMAT.format(new Date());
				UserBean one = new UserBean();
				one.setId(id);
				one.setLocktime(locktime);
				new UserService().update(one);
			}

			Date lockdate = BaseService.PURE_DATETIME_FORMAT.parse(locktime);

			long time = new Date().getTime() - lockdate.getTime();
			long waittime = accountunlockminute * 60 * 1000 - time;
			if (waittime > 0) {
				throw new Exception("账号已锁定，请等待“" + (waittime / 60 / 1000) + 1 + "”分钟后自动解锁，或联系管理员解锁！");
			} else {
				UserBean one = new UserBean();
				one.setId(id);
				one.setStatus(UserStatus.OK.getValue());
				one.setUnlocktime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));
			}
			break;
		case OK:
			break;
		}

		if (ObjectUtil.isTrue(configure.getOpenaccountactivation())) {
			UserActiveStatus activeStatus = UserActiveStatus.get(user.getActivestatus());
			switch (activeStatus) {
			case NOT_ACTIVE:
				throw new Exception("账号暂未激活，请先激活后使用！");
			case OK:
				break;
			}
		}

	}

}
