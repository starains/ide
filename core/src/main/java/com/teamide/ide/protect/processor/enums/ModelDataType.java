package com.teamide.ide.protect.processor.enums;

import com.teamide.util.StringUtil;

public enum ModelDataType {

	ONE("ONE", "单个"),

	LIST("LIST", "列表"),

	PAGE("PAGE", "分页"),

	;

	public static ModelDataType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		ModelDataType type = null;
		for (ModelDataType one : ModelDataType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private ModelDataType(String value, String text) {

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
