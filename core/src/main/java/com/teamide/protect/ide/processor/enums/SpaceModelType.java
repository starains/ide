package com.teamide.protect.ide.processor.enums;

import com.teamide.util.StringUtil;

public enum SpaceModelType {

	SPACE("SPACE", "空间", ModelDataType.ONE),

	PARENTS("PARENTS", "父级", ModelDataType.LIST),

	SPACE_EVENTS("SPACE_EVENTS", "空间事件", ModelDataType.PAGE),

	SPACE_TEAMS("SPACE_TEAMS", "空间团队", ModelDataType.PAGE),

	JOIN_SPACES("JOIN_SPACES", "加入的空间", ModelDataType.PAGE),

	VISIBLE_SPACES("VISIBLE_SPACES", "可见的空间", ModelDataType.PAGE),

	STAR_SPACES("STAR_SPACES", "星标的空间", ModelDataType.PAGE),

	;

	public static SpaceModelType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		SpaceModelType type = null;
		for (SpaceModelType one : SpaceModelType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}

	private SpaceModelType(String value, String text, ModelDataType type) {

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
