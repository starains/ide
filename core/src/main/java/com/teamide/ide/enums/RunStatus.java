package com.teamide.ide.enums;

public enum RunStatus {

	STARTING("STARTING", "启动中"),

	STARTED("STARTED", "已启动"),

	STOPPING("STOPPING", "停止中"),

	STOPPED("STOPPED", "已停止"),

	;

	private RunStatus(String value, String text) {

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
