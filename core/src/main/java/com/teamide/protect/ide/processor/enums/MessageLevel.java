package com.teamide.protect.ide.processor.enums;

import com.teamide.util.StringUtil;

public enum MessageLevel {

	INFO("INFO", "信息"),

	WARN("WARN", "警告"),

	ERROR("ERROR", "错误"),

	SUCCESS("SUCCESS", "成功")

	;

	private MessageLevel(String value, String text) {
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

	public static MessageLevel get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		MessageLevel type = null;
		for (MessageLevel one : MessageLevel.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}
}
