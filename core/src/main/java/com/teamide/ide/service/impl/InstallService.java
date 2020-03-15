package com.teamide.ide.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.teamide.db.bean.Database;
import com.teamide.service.IService;
import com.teamide.util.FileUtil;
import com.teamide.ide.util.PasswordMD5Tool;
import com.teamide.ide.bean.ConfigureBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.configure.IDEOptions;
import com.teamide.ide.constant.IDEConstant;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.service.IConfigureService;
import com.teamide.ide.service.IInstallService;
import com.teamide.ide.service.IRoleService;
import com.teamide.ide.service.IUserService;
import com.alibaba.fastjson.JSONObject;

@Resource
public class InstallService implements IInstallService {

	public boolean installed() {

		if (IDEOptions.get().jdbc == null) {
			return false;
		}
		return IDEConfigure.get() != null;
	}

	public boolean install(JSONObject json) throws Exception {

		if (json == null) {
			throw new Exception("数据为空！");
		}
		if (json.get("database") == null) {
			throw new Exception("数据库数据为空！");
		}
		if (json.get("admin") == null) {
			throw new Exception("admin数据为空！");
		}
		if (json.get("configure") == null) {
			throw new Exception("配置数据为空！");
		}
		Database old_database = IDEOptions.get().jdbc;
		JSONObject database = json.getJSONObject("database");
		saveDatabase(database);
		IDEOptions.loadOptions();
		IService service = IDEFactory.getService();
		if (service == null) {
			throw new Exception("数据库配置有误！");
		}
		try {
			service.queryList("SELECT 1", null);
		} catch (Exception e) {
			if (old_database != null) {
				JSONObject old = (JSONObject) JSONObject.toJSON(old_database);
				saveDatabase(old);
			}
			IDEOptions.loadOptions();
			throw new Exception(e.getMessage());
		}
		IConfigureService configureService = new ConfigureService();

		ConfigureBean configure = json.getJSONObject("configure").toJavaObject(ConfigureBean.class);

		configure.setId("0");
		configureService.save(null, configure);

		IDEConfigure.loadConfigure();

		IUserService userService = new UserService();

		JSONObject admin = json.getJSONObject("admin");
		String email = admin.getString("email");
		String password = admin.getString("password");
		String loginname = admin.getString("loginname");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loginname", loginname);
		UserBean user = null;
		List<UserBean> users = userService.queryList(param);
		if (users.size() > 0) {
			user = users.get(0);
		} else {
			param = new HashMap<String, Object>();
			param.put("email", email);
			users = userService.queryList(param);
			if (users.size() > 0) {
				user = users.get(0);
			}
		}
		if (user == null) {
			user = new UserBean();
		}
		user.setPassword(PasswordMD5Tool.getPasswordMD5(password));
		user.setLoginname(loginname);
		user.setName(loginname);
		user.setEmail(email);

		userService.save(null, user);

		IRoleService roleService = new RoleService();
		roleService.addUserSuperRole(user.getId());
		return true;
	}

	public void saveDatabase(JSONObject database) throws Exception {

		if (database == null) {
			throw new Exception("数据库数据为空！");
		}
		StringBuffer config = new StringBuffer();
		for (String key : database.keySet()) {
			config.append(key);
			config.append("=");
			config.append(database.get(key));
			config.append("\n");
		}
		saveJDBC(config);
	}

	public static final void saveJDBC(StringBuffer jdbc) {

		try {
			FileUtil.write(jdbc.toString().getBytes(), new File(IDEConstant.JDBC));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
