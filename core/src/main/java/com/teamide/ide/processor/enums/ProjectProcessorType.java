package com.teamide.ide.processor.enums;

import java.util.ArrayList;
import java.util.List;

import com.teamide.util.StringUtil;

public enum ProjectProcessorType {

	FILE_SAVE("FILE_SAVE", "文件保存"),

	FILE_PASTE("FILE_PASTE", "文件粘贴"),

	FILE_CLOSE("FILE_CLOSE", "文件关闭"),

	FILE_OPEN("FILE_OPEN", "文件打开"),

	FILE_RENAME("FILE_RENAME", "文件重命名"),

	FILE_MOVE("FILE_MOVE", "文件移动"),

	FILE_CREATE("FILE_CREATE", "文件创建"),

	FILE_DELETE("FILE_DELETE", "文件删除"),

	DOWNLOAD("DOWNLOAD", "下载"),

	UPLOAD("UPLOAD", "上传"),

	MAVEN_CLEAN("MAVEN_CLEAN", "Maven Clean"),

	MAVEN_COMPILE("MAVEN_COMPILE", "Maven Compile"),

	MAVEN_PACKAGE("MAVEN_PACKAGE", "Maven Package"),

	MAVEN_INSTALL("MAVEN_INSTALL", "Maven Install"),

	MAVEN_DEPLOY("MAVEN_DEPLOY", "Maven Deploy"),

	NODE_INSTALL("NODE_INSTALL", "Node Install"),

	SET_PLUGIN_OPTION("SET_PLUGIN_OPTION", "Starter Log Clean"),

	DELETE_PLUGIN_OPTION("DELETE_PLUGIN_OPTION", "Starter Log Clean"),

	;

	public static List<ProjectProcessorType> getList() {
		List<ProjectProcessorType> list = new ArrayList<ProjectProcessorType>();
		for (ProjectProcessorType type : ProjectProcessorType.values()) {
			list.add(type);
		}
		return list;
	}

	private ProjectProcessorType(String value, String text) {
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

	public static ProjectProcessorType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		ProjectProcessorType type = null;
		for (ProjectProcessorType one : ProjectProcessorType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}
}
