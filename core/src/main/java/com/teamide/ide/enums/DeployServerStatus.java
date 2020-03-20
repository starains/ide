package com.teamide.ide.enums;

public enum DeployServerStatus {

	OFFLINE("OFFLINE", "离线"),

	ONLINE("ONLINE", "在线"),

	;

	private DeployServerStatus(String value, String text) {

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

	public String toString() {
		return value;
	}

}
