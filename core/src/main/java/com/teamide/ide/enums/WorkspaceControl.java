package com.teamide.ide.enums;

import com.teamide.ide.bean.WorkspaceControlBean;

public enum WorkspaceControl {

	DATA_LOAD("DATA_LOAD", "数据加载"),

	SPACE_CREATE("SPACE_CREATE", "空间创建"),

	SPACE_UPDATE("SPACE_UPDATE", "空间修改"),

	SPACE_DELETE("SPACE_DELETE", "空间删除"),

	SPACE_TEAM_INSERT("SPACE_TEAM_INSERT", "新增团队"),

	SPACE_TEAM_UPDATE("SPACE_TEAM_UPDATE", "编辑团队"),

	SPACE_TEAM_DELETE("SPACE_TEAM_DELETE", "删除团队"),

	BRANCH_CREATE("BRANCH_CREATE", "版本创建"),

	FILE_SAVE("FILE_SAVE", "文件保存"),

	FILE_PASTE("FILE_PASTE", "文件粘贴"),

	FILE_CLOSE("FILE_CLOSE", "文件关闭"),

	FILE_OPEN("FILE_OPEN", "文件打开"),

	FILE_RENAME("FILE_RENAME", "文件重命名"),

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

	GIT_PUSH_STATUS("GIT_PUSH_STATUS", "Git Push Status"),

	GIT_PULL("GIT_PULL", "Git Pull"),

	GIT_PULL_STATUS("GIT_PULL_STATUS", "Git Pull Status"),

	GIT_BRANCH_CREATE("GIT_BRANCH_CREATE", "Git Branch Create"),

	GIT_BRANCH_DELETE("GIT_BRANCH_DELETE", "Git Branch Delete"),

	GIT_BRANCH_RENAME("GIT_BRANCH_RENAME", "Git Branch Rename"),

	GIT_CHECKOUT("GIT_CHECKOUT", "Git Checkout"),

	GIT_CLONE("GIT_CLONE", "Git Clone"),

	GIT_INIT("GIT_INIT", "Git Init"),

	GIT_REMOTE_ADD("GIT_REMOTE_ADD", "Git Remote Add"),

	GIT_REMOTE_REMOVE("GIT_REMOTE_REMOVE", "Git Remote Remove"),

	GIT_REMOTE_SETURL("GIT_REMOTE_SETURL", "Git Remote Set Url"),

	SET_RUN_OPTION("SET_RUN_OPTION", "设置启动配置"),

	DELETE_RUN_OPTION("DELETE_RUN_OPTION", "删除启动配置"),

	TERMINAL_START("TERMINAL_START", "Terminal Start"),

	TERMINAL_STOP("TERMINAL_STOP", "Terminal Stop"),

	TERMINAL_REMOVE("TERMINAL_REMOVE", "Terminal Remove"),

	TERMINAL_LOG_CLEAN("TERMINAL_LOG_CLEAN", "Terminal Log Clean"),

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

	;

	private WorkspaceControl(WorkspaceControlBean control) {

		this.control = control;
	}

	private WorkspaceControl(String value, String text) {
		this(WorkspaceControlBean.create(value, text));
	}

	private final WorkspaceControlBean control;

	public WorkspaceControlBean getControl() {
		return control;
	}

}
