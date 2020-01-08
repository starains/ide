package com.teamide.ide.bean;

public class WorkspaceDataBean {

	public static WorkspaceDataBean create(String value, String text, boolean list) {
		return new WorkspaceDataBean(value, text, list);
	}

	private WorkspaceDataBean(String value, String text, boolean list) {

		this.value = value;
		this.text = text;
		this.list = list;
	}

	private final String value;
	private final String text;
	private final boolean list;

	public boolean isList() {
		return list;
	}

	public String getValue() {

		return value;
	}

	public String getText() {

		return text;
	}

}
