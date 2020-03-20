package com.teamide.ide.enums;

public enum DeployServerMode {

	SERVER("SERVER", "服务端"),

	CLIENT("CLIENT", "客户端"),

	;

	private DeployServerMode(String value, String text) {

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
