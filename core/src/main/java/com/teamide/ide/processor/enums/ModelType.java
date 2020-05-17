package com.teamide.ide.processor.enums;

import com.teamide.util.StringUtil;

public enum ModelType {

	ONE("ONE", "单个数据", ModelDataType.ONE),

	SESSION("SESSION", "会话", ModelDataType.ONE),

	INSTALLED("INSTALLED", "是否安装", ModelDataType.ONE),

	CONFIGURE("CONFIGURE", "配置", ModelDataType.ONE),

	REMOTES("REMOTES", "远端服务器", ModelDataType.LIST),

	ENVIRONMENTS("ENVIRONMENTS", "环境", ModelDataType.LIST),

	PLUGINS("PLUGINS", "插件", ModelDataType.LIST),

	MASTER_SPACES("MASTER_SPACES", "管理的空间", ModelDataType.PAGE),

	USERS("USERS", "人员", ModelDataType.PAGE),

	USER_LOGINS("USER_LOGINS", "用户登录", ModelDataType.PAGE),

	SEARCH_USERS("SEARCH_USERS", "搜索人员", ModelDataType.PAGE),

	SEARCH_SPACES("SEARCH_SPACES", "搜索空间", ModelDataType.PAGE),

	CERTIFICATES("CERTIFICATES", "密钥", ModelDataType.LIST),

	DATABASES("DATABASES", "数据库", ModelDataType.LIST),

	MANAGE_DATABASES("MANAGE_DATABASES", "数据库", ModelDataType.LIST),

	NGINXS("NGINXS", "Nginx配置", ModelDataType.LIST),

	MANAGE_NGINXS("MANAGE_NGINXS", "Nginx配置", ModelDataType.LIST),

	;

	public static ModelType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		ModelType type = null;
		for (ModelType one : ModelType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private ModelType(String value, String text, ModelDataType type) {

		this.value = value;
		this.text = text;
		this.type = type;
	}

	private final String value;
	private final String text;
	private final ModelDataType type;

	public ModelDataType getType() {
		return type;
	}

	public String getValue() {

		return value;
	}

	public String getText() {

		return text;
	}

}
