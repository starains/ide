package com.teamide.ide.processor.enums;

import java.util.ArrayList;
import java.util.List;

import com.teamide.util.StringUtil;

public enum RepositoryProcessorType {

	BRANCH_CREATE("BRANCH_CREATE", "版本创建"),

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

	STARTER_DEPLOY("STARTER_DEPLOY", "Starter Deploy"),

	STARTER_START("STARTER_START", "Starter Start"),

	STARTER_STOP("STARTER_STOP", "Starter Stop"),

	STARTER_REMOVE("STARTER_REMOVE", "Starter Remove"),

	STARTER_LOG_CLEAN("STARTER_LOG_CLEAN", "Starter Log Clean"),

	SET_PROJECT("SET_PROJECT", "Set Project"),

	;

	public static List<RepositoryProcessorType> getList() {
		List<RepositoryProcessorType> list = new ArrayList<RepositoryProcessorType>();
		for (RepositoryProcessorType type : RepositoryProcessorType.values()) {
			list.add(type);
		}
		return list;
	}

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
