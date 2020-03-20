package com.teamide.ide.processor;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.util.PasswordMD5Tool;
import com.teamide.ide.util.TokenUtil;
import com.teamide.util.StringUtil;
import com.teamide.client.ClientSession;
import com.teamide.ide.IDEShare;
import com.teamide.ide.bean.ConfigureBean;
import com.teamide.ide.bean.DeployServerBean;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.bean.UserPreferenceBean;
import com.teamide.ide.processor.enums.ProcessorType;
import com.teamide.ide.processor.param.ProcessorParam;
import com.teamide.ide.service.IConfigureService;
import com.teamide.ide.service.IDeployServerService;
import com.teamide.ide.service.IEnvironmentService;
import com.teamide.ide.service.IInstallService;
import com.teamide.ide.service.ILoginService;
import com.teamide.ide.service.IUserService;
import com.teamide.ide.service.impl.ConfigureService;
import com.teamide.ide.service.impl.DeployServerService;
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
		case CONFIGURE_UPDATE:
			IConfigureService configureService = new ConfigureService();
			ConfigureBean configure = data.toJavaObject(ConfigureBean.class);
			configure.setId("0");
			configureService.update(this.param.getSession(), configure);

			break;
		case ENVIRONMENT_CREATE:
			IEnvironmentService environmentService = new EnvironmentService();
			EnvironmentBean environment = data.toJavaObject(EnvironmentBean.class);
			environmentService.insert(this.param.getSession(), environment);

			break;
		case ENVIRONMENT_DELETE:
			environmentService = new EnvironmentService();
			environment = environmentService.get(data.getString("id"));
			environmentService.delete(this.param.getSession(), environment);

			break;
		case ENVIRONMENT_UPDATE:
			environmentService = new EnvironmentService();
			environment = data.toJavaObject(EnvironmentBean.class);
			environmentService.update(this.param.getSession(), environment);

			break;

		case DEPLOY_SERVER_CREATE:
			IDeployServerService deployServerService = new DeployServerService();
			DeployServerBean deployServer = data.toJavaObject(DeployServerBean.class);
			deployServerService.insert(this.param.getSession(), deployServer);

			break;
		case DEPLOY_SERVER_DELETE:
			deployServerService = new DeployServerService();
			deployServer = deployServerService.get(data.getString("id"));
			deployServerService.delete(this.param.getSession(), deployServer);

			break;
		case DEPLOY_SERVER_UPDATE:
			deployServerService = new DeployServerService();
			deployServer = data.toJavaObject(DeployServerBean.class);
			deployServerService.update(this.param.getSession(), deployServer);

			break;
		}

		return null;
	}

	private UserBean doRegister(JSONObject data) throws Exception {
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
