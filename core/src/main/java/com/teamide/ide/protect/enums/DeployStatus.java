package com.teamide.ide.protect.enums;

public enum DeployStatus {

	NOT_DEPLOYED("NOT_DEPLOYED", "未部署"),

	DEPLOYING("DEPLOYING", "部署中"),

	DEPLOYED("DEPLOYED", "已部署"),

	;

	private DeployStatus(String value, String text) {

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
