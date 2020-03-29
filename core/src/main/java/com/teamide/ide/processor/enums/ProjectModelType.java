package com.teamide.ide.processor.enums;

import com.teamide.util.StringUtil;

public enum ProjectModelType {

	FILE("FILE", "文件", ModelDataType.ONE),

	FILES("FILES", "文件列表", ModelDataType.LIST),

	PROJECT("PROJECT", "项目", ModelDataType.ONE),

	PLUGIN_OPTION("PLUGIN_OPTION", "库", ModelDataType.ONE),

	;

	public static ProjectModelType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		ProjectModelType type = null;
		for (ProjectModelType one : ProjectModelType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private ProjectModelType(String value, String text, ModelDataType type) {

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
