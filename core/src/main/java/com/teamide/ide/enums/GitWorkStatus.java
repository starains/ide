package com.teamide.ide.enums;

public enum GitWorkStatus {

	PUSHING("PUSHING", "推送中"),

	PUSHED("PUSHED", "推送结束"),

	PUSHERR("PUSHERR", "推送异常"),

	PULLING("PULLING", "拉取中"),

	PULLED("PULLED", "拉取结束"),

	PULLERR("PULLERR", "拉取异常"),

	CHECKOUTING("CHECKOUTING", "检出中"),

	CHECKOUTED("CHECKOUTED", "检出结束"),

	CHECKOUTERR("CHECKOUTERR", "检出异常"),

	;

	private GitWorkStatus(String value, String text) {

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
