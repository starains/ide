package com.teamide.starter.enums;

public enum DeployStatus {

	DEPLOYING("DEPLOYING", "安装中"),

	DEPLOYED("DEPLOYED", "安装完成"),

	UPLOADING("UPLOADING", "安装中"),

	UPLOADED("UPLOADED", "安装完成"),

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
