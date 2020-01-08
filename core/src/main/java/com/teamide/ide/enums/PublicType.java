package com.teamide.ide.enums;

import com.teamide.util.StringUtil;

public enum PublicType {

	OPEN("OPEN", "开放"),

	PRIVATE("PRIVATE", "私有"),

	;

	public static PublicType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		PublicType type = null;
		for (PublicType one : PublicType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private PublicType(String value, String text) {

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
