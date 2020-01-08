package com.teamide.protect.ide.processor;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.util.PasswordMD5Tool;
import com.teamide.util.StringUtil;
import com.teamide.ide.IDEShare;
import com.teamide.ide.bean.ConfigureBean;
import com.teamide.ide.bean.EnvironmentBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.bean.UserPreferenceBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.service.IConfigureService;
import com.teamide.ide.service.IEnvironmentService;
import com.teamide.ide.service.IInstallService;
import com.teamide.ide.service.ILoginService;
import com.teamide.ide.service.IUserService;
import com.teamide.protect.ide.engine.EngineSession;
import com.teamide.protect.ide.processor.enums.MessageLevel;
import com.teamide.protect.ide.processor.enums.ModelType;
import com.teamide.protect.ide.processor.enums.ProcessorType;
import com.teamide.protect.ide.processor.param.ProcessorParam;
import com.teamide.protect.ide.service.ConfigureService;
import com.teamide.protect.ide.service.EnvironmentService;
import com.teamide.protect.ide.service.InstallService;
import com.teamide.protect.ide.service.LoginService;
import com.teamide.protect.ide.service.UserPreferenceService;
import com.teamide.protect.ide.service.UserService;

public class Processor extends ProcessorData {

	public Processor(EngineSession session, ProcessorParam param) {
		super(session, param);
	}

	public final void on(String messageID, JSONObject json) {
		if (json == null || json.size() == 0) {
			return;
		}
		String type = json.getString("type");
		JSONObject data = json.getJSONObject("data");
		if (data == null) {
			data = new JSONObject();
		}
		try {
			process(messageID, type, data);
		} catch (Exception e) {
			if (e instanceof NullPointerException) {
				e.printStackTrace();
			}
			outMessage(MessageLevel.ERROR, e.getMessage());
		}
	}

	protected void process(String messageID, String type, JSONObject data) throws Exception {
		ProcessorType processorType = ProcessorType.get(type);
		if (processorType == null) {
			return;
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
				service.save(param.getClient(), one);
			}
			break;
		case VALIDATE:
			out(messageID, processorType.getValue(), data);
			break;
		case DATA:
			String model = data.getString("model");
			onData(messageID, model, data);
			break;
		case MESSAGE:
			break;
		case RESTART:
			IDEShare.addEvent("RESTART");
			break;
		case INSTALL:
			try {
				IInstallService installService = new InstallService();
				installService.install(data);
				onData(messageID, ModelType.INSTALLED, new JSONObject());
				outMessage(MessageLevel.SUCCESS, "安装成功.");
			} catch (Exception e) {
				outMessage(MessageLevel.ERROR, "安装失败.");
				outMessage(MessageLevel.ERROR, e.getMessage());
				onData(messageID, ModelType.INSTALLED, new JSONObject());
			}
			break;
		case REGISTER:
			UserBean user = doRegister(data);
			out(messageID, ProcessorType.REGISTER.getValue(), user);
			break;
		case LOGIN:
			doLogin(data);
			break;
		case LOGOUT:
			doLogout(data);
			break;
		case CONFIGURE_UPDATE:
			IConfigureService configureService = new ConfigureService();
			ConfigureBean configure = data.toJavaObject(ConfigureBean.class);
			configure.setId("0");
			configureService.update(this.param.getClient(), configure);
			outMessage(MessageLevel.SUCCESS, "修改成功！");
			onData(messageID, ModelType.CONFIGURE, new JSONObject());
			break;
		case ENVIRONMENT_CREATE:
			IEnvironmentService environmentService = new EnvironmentService();
			EnvironmentBean environment = data.toJavaObject(EnvironmentBean.class);
			environmentService.insert(this.param.getClient(), environment);
			outMessage(MessageLevel.SUCCESS, "创建成功！");
			onData(messageID, ModelType.ENVIRONMENTS, new JSONObject());
			break;
		case ENVIRONMENT_DELETE:
			environmentService = new EnvironmentService();
			environment = environmentService.get(data.getString("id"));
			environmentService.delete(this.param.getClient(), environment);
			outMessage(MessageLevel.SUCCESS, "删除成功！");
			onData(messageID, ModelType.ENVIRONMENTS, new JSONObject());
			break;
		case ENVIRONMENT_UPDATE:
			environmentService = new EnvironmentService();
			environment = data.toJavaObject(EnvironmentBean.class);
			environmentService.update(this.param.getClient(), environment);
			outMessage(MessageLevel.SUCCESS, "修改成功！");
			onData(messageID, ModelType.ENVIRONMENTS, new JSONObject());
			break;

		}
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
		Client client = this.param.getClient();

		String loginname = data.getString("loginname");
		String password = data.getString("password");
		if (StringUtil.isEmpty(loginname)) {
			throw new Exception("登录名称不能为空.");
		}
		if (StringUtil.isEmpty(password)) {
			throw new Exception("密码不能为空.");
		}
		ILoginService loginService = new LoginService();
		loginService.doLogin(client, loginname, password);
		if (client.isLogin()) {
			JSONObject message = new JSONObject();
			message.put("type", ProcessorType.LOGIN.getValue());
			message.put("data", data);
			outBySessionid(message);
		}
	}

	private void doLogout(JSONObject data) throws Exception {
		Client client = this.param.getClient();

		try {
			client.doLogout();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		JSONObject message = new JSONObject();
		message.put("type", ProcessorType.LOGOUT.getValue());
		outBySessionid(message);
	}
}
