package com.teamide.terminal;

public enum Event {

	START("START", "启动"),

	STOP("STOP", "停止"),

	DESTROY("DESTROY", "销毁"),

	;

	private Event(String value, String text) {

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
