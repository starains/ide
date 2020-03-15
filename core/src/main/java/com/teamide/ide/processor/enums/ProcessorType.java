package com.teamide.ide.processor.enums;

import com.teamide.util.StringUtil;

public enum ProcessorType {

	VALIDATE("VALIDATE", "Validate"),

	INSTALL("INSTALL", "安装"),

	RESTART("RESTART", "重启"),

	LOGIN("LOGIN", "登录"),

	AUTO_LOGIN("AUTO_LOGIN", "自动登录"),

	LOGOUT("LOGOUT", "登出"),

	REGISTER("REGISTER", "注册"),

	CONFIGURE_UPDATE("CONFIGURE_UPDATE", "配置修改"),

	ENVIRONMENT_CREATE("ENVIRONMENT_CREATE", "环境创建"),

	ENVIRONMENT_UPDATE("ENVIRONMENT_UPDATE", "环境修改"),

	ENVIRONMENT_DELETE("ENVIRONMENT_DELETE", "环境删除"),

	PREFERENCE("PREFERENCE", "偏好"),

	;

	private ProcessorType(String value, String text) {
		this.value = value;
		this.text = text;
	}

	private final String value;

	private final String text;

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	public static ProcessorType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		ProcessorType type = null;
		for (ProcessorType one : ProcessorType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}
}
