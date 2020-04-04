package com.teamide.ide.enums;

import com.teamide.util.StringUtil;

public enum SpacePermission {

	MASTER("MASTER", "管理员"),

	DEVELOPER("DEVELOPER", "开发者"),

	VIEWER("VIEWER", "观察者"),

	NO("NO", "无权限"),

	;

	public static SpacePermission get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		SpacePermission type = null;
		for (SpacePermission one : SpacePermission.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private SpacePermission(String value, String text) {

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

}
