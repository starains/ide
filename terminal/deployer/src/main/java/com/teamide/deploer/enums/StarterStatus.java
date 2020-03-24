package com.teamide.deploer.enums;

public enum StarterStatus {

	STARTING("STARTING", "启动"),

	STARTED("STARTED", "启动"),

	STOPPING("STOPPING", "停止"),

	STOPPED("STOPPED", "停止"),

	DESTROYING("DESTROYING", "销毁"),

	DESTROYED("DESTROYED", "销毁"),

	;

	private StarterStatus(String value, String text) {

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
