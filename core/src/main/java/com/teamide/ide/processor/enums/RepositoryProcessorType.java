package com.teamide.ide.processor.enums;

import com.teamide.util.StringUtil;

public enum RepositoryProcessorType {

	BRANCH_CREATE("BRANCH_CREATE", "版本创建"),

	FILE_SAVE("FILE_SAVE", "文件保存"),

	FILE_PASTE("FILE_PASTE", "文件粘贴"),

	FILE_CLOSE("FILE_CLOSE", "文件关闭"),

	FILE_OPEN("FILE_OPEN", "文件打开"),

	FILE_RENAME("FILE_RENAME", "文件重命名"),

	FILE_MOVE("FILE_MOVE", "文件移动"),

	FILE_CREATE("FILE_CREATE", "文件创建"),

	FILE_DELETE("FILE_DELETE", "文件删除"),

	FILE_DOWNLOAD("FILE_DOWNLOAD", "文件下载"),

	MAVEN_CLEAN("MAVEN_CLEAN", "Maven Clean"),

	MAVEN_COMPILE("MAVEN_COMPILE", "Maven Compile"),

	MAVEN_PACKAGE("MAVEN_PACKAGE", "Maven Package"),

	MAVEN_INSTALL("MAVEN_INSTALL", "Maven Install"),

	MAVEN_DEPLOY("MAVEN_DEPLOY", "Maven Deploy"),

	GIT_REVERT("GIT_REVERT", "Git Revert"),

	GIT_PUSH("GIT_PUSH", "Git Push"),

	GIT_PULL("GIT_PULL", "Git Pull"),

	GIT_INDEX_ADD("GIT_INDEX_ADD", "Git Index Add"),

	GIT_INDEX_REMOVE("GIT_INDEX_REMOVE", "Git Index Remove"),

	GIT_BRANCH_CREATE("GIT_BRANCH_CREATE", "Git Branch Create"),

	GIT_BRANCH_DELETE("GIT_BRANCH_DELETE", "Git Branch Delete"),

	GIT_BRANCH_RENAME("GIT_BRANCH_RENAME", "Git Branch Rename"),

	GIT_CHECKOUT("GIT_CHECKOUT", "Git Checkout"),

	GIT_CLONE("GIT_CLONE", "Git Clone"),

	GIT_INIT("GIT_INIT", "Git Init"),

	GIT_REMOTE_ADD("GIT_REMOTE_ADD", "Git Remote Add"),

	GIT_REMOTE_REMOVE("GIT_REMOTE_REMOVE", "Git Remote Remove"),

	GIT_REMOTE_SETURL("GIT_REMOTE_SETURL", "Git Remote Set Url"),

	SET_STARTER_OPTION("SET_STARTER_OPTION", "设置启动配置"),

	DELETE_STARTER_OPTION("DELETE_STARTER_OPTION", "删除启动配置"),

	STARTER_START("STARTER_START", "Starter Start"),

	STARTER_STOP("STARTER_STOP", "Starter Stop"),

	STARTER_DESTROY("STARTER_DESTROY", "Starter Destroy"),

	STARTER_REMOVE("STARTER_REMOVE", "Starter Remove"),

	STARTER_LOG_CLEAN("STARTER_LOG_CLEAN", "Starter Log Clean"),

	RUNNER_DEPLOY("RUNNER_DEPLOY", "Runner Deploy"),

	RUNNER_START("RUNNER_START", "Runner Start"),

	RUNNER_STOP("RUNNER_STOP", "Runner Stop"),

	RUNNER_REMOVE("RUNNER_REMOVE", "Runner Remove"),

	RUNNER_LOG_CLEAN("RUNNER_LOG_CLEAN", "Runner Log Clean"),

	SET_RUNNER_OPTION("SET_RUNNER_OPTION", "设置远程部署器配置"),

	DELETE_RUNNER_OPTION("DELETE_RUNNER_OPTION", "删除远程部署器配置"),

	RUNNER_SERVER_SAVE("RUNNER_SERVER_SAVE", "保存远程服务器配置"),

	RUNNER_SERVER_DELETE("RUNNER_SERVER_DELETE", "删除远程服务器配置"),

	RUNNER_CLIENT_SAVE("RUNNER_CLIENT_SAVE", "保存远程客户端配置"),

	RUNNER_CLIENT_DELETE("RUNNER_CLIENT_DELETE", "删除远程客户端配置"),

	APP_SET_OPTION("APP_SET_OPTION", "应用配置"),

	APP_DELETE_OPTION("APP_DELETE_OPTION", "删除应用配置"),

	APP_GENERATE_SOURCE_CODE("APP_GENERATE_SOURCE_CODE", "应用生成源码"),

	;

	private RepositoryProcessorType(String value, String text) {
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

	public static RepositoryProcessorType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		RepositoryProcessorType type = null;
		for (RepositoryProcessorType one : RepositoryProcessorType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}
}
