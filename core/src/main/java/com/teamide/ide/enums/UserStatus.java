package com.teamide.ide.enums;

import com.teamide.util.StringUtil;

public enum UserStatus {

	OK(1, "正常"),

	LOCK(2, "锁定"),

	DISABLE(3, "禁用"),

	DESTROY(0, "注销"),

	;

	public static UserStatus get(Integer value) {
		if (value == 0) {
			return null;
		}

		return get(String.valueOf(value));
	}

	public static UserStatus get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		UserStatus status = null;
		for (UserStatus one : UserStatus.values()) {
			if (String.valueOf(one.getValue()).equalsIgnoreCase(value)) {
				status = one;
				break;
			}
		}
		return status;
	}

	private UserStatus(Integer value, String text) {

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
