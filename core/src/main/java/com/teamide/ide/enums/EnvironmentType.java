package com.teamide.ide.enums;

import com.teamide.util.StringUtil;

public enum EnvironmentType {

	JAVA("JAVA", "Java"),

	MAVEN("MAVEN", "Maven"),

	TOMCAT("TOMCAT", "Tomcat"),

	GIT("GIT", "Git"),

	NODE("NODE", "Node"),

	PYTHON("PYTHON", "Python"),

	;

	public static EnvironmentType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		EnvironmentType type = null;
		for (EnvironmentType one : EnvironmentType.values()) {
			if (one.getValue().equalsIgnoreCase(value) || one.name().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private EnvironmentType(String value, String text) {

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
