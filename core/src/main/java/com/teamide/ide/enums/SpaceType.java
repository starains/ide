package com.teamide.ide.enums;

import com.teamide.util.StringUtil;

public enum SpaceType {

	USERS("USERS", "用户"),

	REPOSITORYS("REPOSITORYS", "库"),

	ORGANIZATIONS("ORGANIZATIONS", "组织"),

	ENTERPRISES("ENTERPRISES", "企业"),

	PRODUCTS("PRODUCTS", "产品"),

	;

	public static SpaceType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		SpaceType type = null;
		for (SpaceType one : SpaceType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private SpaceType(String value, String text) {

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
