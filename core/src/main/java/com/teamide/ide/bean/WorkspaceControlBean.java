package com.teamide.ide.bean;

public class WorkspaceControlBean {

	public static WorkspaceControlBean create(String value, String text) {
		return new WorkspaceControlBean(value, text);
	}

	private WorkspaceControlBean(String value, String text) {

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
