package com.teamide.ide.client;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.teamide.ide.bean.RoleBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.util.StringUtil;

public class Client {

	private final HttpSession session;

	private boolean login;

	private boolean manager;

	private UserBean user;

	private List<RoleBean> roles;

	private Set<String> permissions;

	public Client(HttpSession session) {

		this.session = session;
	}

	public Client doLogin(UserBean user) {

		if (user != null && StringUtil.isNotEmpty(user.getId())) {
			this.login = true;
			this.user = user;
		} else {
			this.login = false;
			this.user = null;
		}

		return this;
	}

	public Client doLogout() {

		this.login = false;
		this.user = null;

		return this;
	}

	public HttpSession getSession() {

		return session;
	}

	public boolean isLogin() {

		return login;
	}

	public List<RoleBean> getRoles() {

		return roles;
	}

	public void setRoles(List<RoleBean> roles) {

		manager = false;
		if (roles != null) {
			for (RoleBean role : roles) {
				if (role.isForsuper()) {
					manager = true;
				}
			}
		}

		this.roles = roles;
	}

	public Set<String> getPermissions() {

		return permissions;
	}

	public void setPermissions(Set<String> permissions) {

		this.permissions = permissions;
	}

	public boolean isManager() {

		return manager;
	}

	public UserBean getUser() {
		return user;
	}

}
