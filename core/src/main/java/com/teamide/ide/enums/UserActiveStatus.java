package com.teamide.ide.enums;

import com.teamide.util.StringUtil;

public enum UserActiveStatus {

	OK(1, "正常"),

	NOT_ACTIVE(0, "未激活"),

	;

	public static UserActiveStatus get(Integer value) {
		if (value == 0) {
			return null;
		}

		return get(String.valueOf(value));
	}

	public static UserActiveStatus get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		UserActiveStatus status = null;
		for (UserActiveStatus one : UserActiveStatus.values()) {
			if (String.valueOf(one.getValue()).equalsIgnoreCase(value)) {
				status = one;
				break;
			}
		}
		return status;
	}

	private UserActiveStatus(Integer value, String text) {

		this.value = value;
		this.text = text;
	}

	private final Integer value;
	private final String text;

	public Integer getValue() {

		return value;
	}

	public String getText() {

		return text;
	}

}
