package com.teamide.ide.processor.enums;

import com.teamide.util.StringUtil;

public enum RepositoryModelType {

	REPOSITORY_STATUS("REPOSITORY_STATUS", "库状态", ModelDataType.ONE),

	REPOSITORY("REPOSITORY", "库", ModelDataType.ONE),

	BRANCHS("BRANCHS", "版本", ModelDataType.LIST),

	FILE("FILE", "文件", ModelDataType.ONE),

	FILES("FILES", "文件列表", ModelDataType.LIST),

	PROJECT("PROJECT", "项目", ModelDataType.ONE),

	GIT("GIT", "库", ModelDataType.ONE),

	GIT_BRANCH_LIST("GIT_BRANCH_LIST", "库", ModelDataType.ONE),

	GIT_LOG("GIT_LOG", "库", ModelDataType.ONE),

	GIT_REMOTE_LIST("GIT_REMOTE_LIST", "库", ModelDataType.ONE),

	GIT_STATUS("GIT_STATUS", "库", ModelDataType.ONE),

	GIT_WORK_STATUS("GIT_WORK_STATUS", "Git工作状态", ModelDataType.ONE),

	STARTER_OPTIONS("STARTER_OPTIONS", "库", ModelDataType.ONE),

	STARTERS("STARTERS", "库", ModelDataType.ONE),

	STARTER_STATUS("STARTER_STATUS", "库", ModelDataType.ONE),

	STARTER_LOG("STARTER_LOG", "库", ModelDataType.ONE),

	DEPLOYER_LOG("DEPLOYER_LOG", "库", ModelDataType.ONE),

	DEPLOYER_STATUS("DEPLOYER_STATUS", "库", ModelDataType.ONE),

	DEPLOYER_OPTIONS("DEPLOYER_OPTIONS", "库", ModelDataType.ONE),

	DEPLOYERS("DEPLOYERS", "库", ModelDataType.ONE),

	APP("APP", "库", ModelDataType.ONE),

	;

	public static RepositoryModelType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		RepositoryModelType type = null;
		for (RepositoryModelType one : RepositoryModelType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private RepositoryModelType(String value, String text, ModelDataType type) {

		this.value = value;
		this.text = text;
		this.type = type;
	}

	private final String value;
	private final String text;
	private final ModelDataType type;

	public ModelDataType getType() {
		return type;
	}

	public String getValue() {

		return value;
	}

	public String getText() {

		return text;
	}

}
