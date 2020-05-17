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

	USER_UPDATE("USER_UPDATE", "修改用户"),

	USER_INSERT("USER_INSERT", "新增用户"),

	USER_DISABLE("USER_DISABLE", "禁用用户"),

	USER_ENABLE("USER_ENABLE", "启用用户"),

	USER_LOCK("USER_LOCK", "锁定用户"),

	USER_UNLOCK("USER_UNLOCK", "解锁用户"),

	USER_ACTIVE("USER_ACTIVE", "激活用户"),

	UPDATE_PASSWORD("UPDATE_PASSWORD", "修改密码"),

	CONFIGURE_UPDATE("CONFIGURE_UPDATE", "配置修改"),

	ENVIRONMENT_CREATE("ENVIRONMENT_CREATE", "环境创建"),

	ENVIRONMENT_UPDATE("ENVIRONMENT_UPDATE", "环境修改"),

	ENVIRONMENT_DELETE("ENVIRONMENT_DELETE", "环境删除"),

	NGINX_CREATE("NGINX_CREATE", "Nginx配置创建"),

	NGINX_UPDATE("NGINX_UPDATE", "Nginx配置修改"),

	NGINX_DELETE("NGINX_DELETE", "Nginx配置删除"),

	NGINX_APPLY("NGINX_APPLY", "Nginx配置申请"),

	PREFERENCE("PREFERENCE", "偏好"),

	REMOTE_CREATE("REMOTE_CREATE", "远程服务器创建"),

	REMOTE_UPDATE("REMOTE_UPDATE", "远程服务器修改"),

	REMOTE_DELETE("REMOTE_DELETE", "远程服务器删除"),

	PLUGIN_CREATE("PLUGIN_CREATE", "插件创建"),

	PLUGIN_UPDATE("PLUGIN_UPDATE", "插件修改"),

	PLUGIN_DELETE("PLUGIN_DELETE", "插件删除"),

	CERTIFICATE_CREATE("CERTIFICATE_CREATE", "密钥创建"),

	CERTIFICATE_UPDATE("CERTIFICATE_UPDATE", "密钥修改"),

	CERTIFICATE_DELETE("CERTIFICATE_DELETE", "密钥删除"),

	DATABASE_CREATE("DATABASE_CREATE", "数据库创建"),

	DATABASE_UPDATE("DATABASE_UPDATE", "数据库修改"),

	DATABASE_DELETE("DATABASE_DELETE", "数据库删除"),

	DATABASE_APPLY("DATABASE_APPLY", "数据库申请"),

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
