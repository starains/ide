package com.teamide.ide.processor;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.util.PasswordMD5Tool;
import com.teamide.ide.util.TokenUtil;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;
import com.teamide.client.ClientSession;
import com.teamide.ide.IDEShare;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.bean.RemoteBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.bean.UserPreferenceBean;
import com.teamide.ide.configure.IDEConfigure;
import com.teamide.ide.enums.UserActiveStatus;
import com.teamide.ide.enums.UserStatus;
import com.teamide.ide.exception.NoPermissionException;
import com.teamide.ide.param.ProcessorParam;
import com.teamide.ide.plugin.PluginHandler;
import com.teamide.ide.plugin.PluginLoader;
import com.teamide.ide.processor.enums.ProcessorType;
import com.teamide.ide.service.IConfigureService;
import com.teamide.ide.service.IRemoteService;
import com.teamide.ide.service.IEnvironmentService;
import com.teamide.ide.service.IInstallService;
import com.teamide.ide.service.ILoginService;
import com.teamide.ide.service.IUserService;
import com.teamide.ide.service.impl.BaseService;
import com.teamide.ide.service.impl.ConfigureService;
import com.teamide.ide.service.impl.RemoteService;
import com.teamide.ide.service.impl.EnvironmentService;
import com.teamide.ide.service.impl.InstallService;
import com.teamide.ide.service.impl.LoginService;
import com.teamide.ide.service.impl.UserPreferenceService;
import com.teamide.ide.service.impl.UserService;

public class Processor extends ProcessorLoad {

	public Processor(ProcessorParam param) {
		super(param);
	}

	public Object onDo(String type, JSONObject data) throws Exception {
		ProcessorType processorType = ProcessorType.get(type);
		return onDo(processorType, data);
	}

	protected Object onDo(ProcessorType processorType, JSONObject data) throws Exception {
		if (processorType == null) {
			return null;
		}

		SpaceEventBean spaceEventBean = new SpaceEventBean();
		spaceEventBean.setType(processorType.getValue());
		spaceEventBean.setName(processorType.getText());
		switch (processorType) {
		case PREFERENCE:
			String id = data.getString("id");
			if (!StringUtil.isEmpty(data.getString("id")) && data.get("option") != null) {

				UserPreferenceService service = new UserPreferenceService();
				UserPreferenceBean one = service.get(id);
				JSONObject option = new JSONObject();
				if (one != null && !StringUtil.isEmpty(one.getOption())) {
					option = JSONObject.parseObject(one.getOption());
				}
				option.putAll(data.getJSONObject("option"));
				one = new UserPreferenceBean();
				one.setId(id);
				one.setOption(option.toJSONString());
				service.save(param.getSession(), one);
			}
			break;
		case VALIDATE:
			break;
		case RESTART:
			IDEShare.addEvent("RESTART");
			break;
		case INSTALL:
			IInstallService installService = new InstallService();
			installService.install(data);
			break;
		case REGISTER:
			doRegister(data);
			break;
		case LOGIN:
			doLogin(data);
			break;
		case AUTO_LOGIN:
			doAutoLogin(data);
			break;
		case LOGOUT:
			doLogout(data);
			break;
		case UPDATE_PASSWORD:
			if (this.param.getSession().getUser() == null) {
				throw new Exception("用户登录信息丢失，请先登录！");
			}
			String oldpassword = data.getString("oldpassword");
			String newpassword = data.getString("newpassword");
			String repassword = data.getString("repassword");
			if (StringUtil.isEmpty(oldpassword)) {
				throw new Exception("请输入原密码！");
			}
			if (StringUtil.isEmpty(newpassword)) {
				throw new Exception("请输入新密码！");
			}
			if (StringUtil.isEmpty(repassword)) {
				throw new Exception("请确认新密码！");
			}
			if (!newpassword.equals(repassword)) {
				throw new Exception("密码输入不一致，请重新确认密码！");
			}
			UserService userService = new UserService();
			UserBean user = userService.get(this.param.getSession().getUser().getId());
			oldpassword = PasswordMD5Tool.getPasswordMD5(oldpassword);
			newpassword = PasswordMD5Tool.getPasswordMD5(newpassword);
			repassword = PasswordMD5Tool.getPasswordMD5(repassword);

			if (!oldpassword.equalsIgnoreCase(user.getPassword())) {
				throw new Exception("原密码输入有误，请重新输入！");
			}
			UserBean updateUser = new UserBean();
			updateUser.setId(user.getId());
			updateUser.setPassword(newpassword);
			userService.update(this.param.getSession(), updateUser);
			break;
		case CONFIGURE_UPDATE:
			checkPermission(processorType);

			IConfigureService configureService = new ConfigureService();
			IDEConfigure configure = data.toJavaObject(IDEConfigure.class);
			configureService.save(this.param.getSession(), configure);

			break;
		case ENVIRONMENT_CREATE:
			checkPermission(processorType);

			IEnvironmentService environmentService = new EnvironmentService();
			EnvironmentBean environment = data.toJavaObject(EnvironmentBean.class);
			environmentService.insert(this.param.getSession(), environment);

			break;
		case ENVIRONMENT_DELETE:
			checkPermission(processorType);

			environmentService = new EnvironmentService();
			environment = environmentService.get(data.getString("id"));
			environmentService.delete(this.param.getSession(), environment);

			break;
		case ENVIRONMENT_UPDATE:
			checkPermission(processorType);

			environmentService = new EnvironmentService();
			environment = data.toJavaObject(EnvironmentBean.class);
			environmentService.update(this.param.getSession(), environment);

			break;

		case REMOTE_CREATE:
			checkPermission(processorType);

			IRemoteService remoteService = new RemoteService();
			RemoteBean remote = data.toJavaObject(RemoteBean.class);
			remoteService.insert(this.param.getSession(), remote);

			break;
		case REMOTE_DELETE:
			checkPermission(processorType);

			remoteService = new RemoteService();
			remote = remoteService.get(data.getString("id"));
			remoteService.delete(this.param.getSession(), remote);

			break;
		case REMOTE_UPDATE:
			checkPermission(processorType);

			remoteService = new RemoteService();
			remote = data.toJavaObject(RemoteBean.class);
			remoteService.update(this.param.getSession(), remote);

			break;

		case PLUGIN_CREATE:
			checkPermission(processorType);

			String name = data.getString("name");
			String version = data.getString("version");
			PluginHandler.getPlugin(name, version);

			break;
		case PLUGIN_DELETE:
			checkPermission(processorType);

			name = data.getString("name");
			version = data.getString("version");

			PluginLoader loader = PluginHandler.getPluginLoader(name, version);
			if (loader != null) {
				loader.close();
				loader.getJarFile().delete();
			}
			break;
		case PLUGIN_UPDATE:
			checkPermission(processorType);

			break;
		case USER_INSERT:
			checkPermission(processorType);
			
			user = data.toJavaObject(UserBean.class);
			new UserService().save(param.getSession(), user);
			break;
		case USER_UPDATE:
			checkPermission(processorType);
			
			id = data.getString("id");
			if (StringUtil.isNotEmpty(id)) {
				user = data.toJavaObject(UserBean.class);
				new UserService().update(param.getSession(), user);
			}
			break;
		case USER_ACTIVE:
			checkPermission(processorType);
			
			id = data.getString("id");
			if (StringUtil.isNotEmpty(id)) {
				user = new UserBean();
				user.setId(id);
				user.setActivestatus(UserActiveStatus.OK.getValue());
				user.setActivetime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));
				new UserService().update(param.getSession(), user);
			}
			break;
		case USER_DISABLE:
			checkPermission(processorType);
			
			id = data.getString("id");
			if (StringUtil.isNotEmpty(id)) {
				user = new UserBean();
				user.setId(id);
				user.setStatus(UserStatus.DISABLE.getValue());
				user.setDisabletime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));
				new UserService().update(param.getSession(), user);
			}
			break;
		case USER_ENABLE:
			checkPermission(processorType);
			
			id = data.getString("id");
			if (StringUtil.isNotEmpty(id)) {
				user = new UserBean();
				user.setId(id);
				user.setStatus(UserStatus.OK.getValue());
				user.setEnabletime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));
				new UserService().update(param.getSession(), user);
			}
			break;
		case USER_LOCK:
			checkPermission(processorType);
			
			id = data.getString("id");
			if (StringUtil.isNotEmpty(id)) {
				user = new UserBean();
				user.setId(id);
				user.setStatus(UserStatus.LOCK.getValue());
				user.setLocktime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));
				new UserService().update(param.getSession(), user);
			}
			break;
		case USER_UNLOCK:
			checkPermission(processorType);
			
			id = data.getString("id");
			if (StringUtil.isNotEmpty(id)) {
				user = new UserBean();
				user.setId(id);
				user.setStatus(UserStatus.OK.getValue());
				user.setUnlocktime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));
				new UserService().update(param.getSession(), user);
			}
			break;
		}

		return null;
	}

	public void checkPermission(ProcessorType type) throws Exception {
		if (!ObjectUtil.isTrue(this.param.getSession().get("isManager"))) {
			throw new NoPermissionException("无权限操作！");
		}
	}

	private UserBean doRegister(JSONObject data) throws Exception {

		IDEConfigure configure = IDEConfigure.get();
		if (configure.getAccount() != null && !ObjectUtil.isTrue(configure.getAccount().getOpenregister())) {
			throw new Exception("未开放注册，请联系管理员！");
		}

		String email = data.getString("email");
		String name = data.getString("name");
		String loginname = data.getString("loginname");
		String password = data.getString("password");
		if (StringUtil.isEmpty(name)) {
			throw new Exception("名称不能为空.");
		}
		if (StringUtil.isEmpty(loginname)) {
			throw new Exception("登录名称不能为空.");
		}
		if (StringUtil.isEmpty(email)) {
			throw new Exception("邮箱不能为空.");
		}
		if (StringUtil.isEmpty(password)) {
			throw new Exception("密码不能为空.");
		}

		UserBean user = new UserBean();
		user.setEmail(email);
		user.setName(name);
		user.setLoginname(loginname);
		user.setPassword(PasswordMD5Tool.getPasswordMD5(password));
		IUserService userService = new UserService();
		if (userService != null) {
			user = userService.register(user);
		}
		if (user != null) {

		} else {
			throw new Exception("注册失败.");
		}
		return user;
	}

	private void doLogin(JSONObject data) throws Exception {
		ClientSession session = this.param.getSession();

		String loginname = data.getString("loginname");
		String password = data.getString("password");
		if (StringUtil.isEmpty(loginname)) {
			throw new Exception("登录名称不能为空.");
		}
		if (StringUtil.isEmpty(password)) {
			throw new Exception("密码不能为空.");
		}
		ILoginService loginService = new LoginService();
		loginService.doLogin(session, loginname, password);
	}

	private void doAutoLogin(JSONObject data) throws Exception {
		ClientSession session = this.param.getSession();

		String token = data.getString("token");
		if (StringUtil.isEmpty(token)) {
			throw new Exception("用户登录令牌信息丢失.");
		}
		JSONObject json = TokenUtil.getJSON(token);
		String id = json.getString("id");
		if (StringUtil.isEmpty(id)) {
			throw new Exception("登录信息丢失.");
		}
		ILoginService loginService = new LoginService();
		loginService.doLoginById(session, id);
	}

	private void doLogout(JSONObject data) throws Exception {
		ClientSession session = this.param.getSession();
		session.doLogout();
	}
}
