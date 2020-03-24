package com.teamide.deploer.enums;

public enum InstallStatus {

	INSTALL_STARER("INSTALL_STARER", "启动"),

	INSTALL_STARER_ERROR("INSTALL_STARER_ERROR", "启动"),

	INSTALL_SERVER("INSTALL_SERVER", "停止"),

	INSTALL_SERVER_ERROR("INSTALL_SERVER_ERROR", "停止"),

	INSTALL_PROJECT_ING("INSTALL_PROJECT_ING", "销毁"),

	INSTALL_PROJECT_ED("INSTALL_PROJECT_ED", "销毁"),

	INSTALL_PROJECT_ERROR("INSTALL_PROJECT_ERROR", "销毁"),

	INSTALL_SHELL("INSTALL_SHELL", "销毁"),

	INSTALL_SHELL_ERROR("INSTALL_SHELL_ERROR", "销毁"),

	WORK_UPLOADING("WORK_UPLOADING", "安装中"),

	WORK_UPLOADED("WORK_UPLOADED", "安装完成"),

	;

	private InstallStatus(String value, String text) {

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
