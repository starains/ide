package com.teamide.ide.processor.enums;

import java.util.ArrayList;
import java.util.List;

import com.teamide.util.StringUtil;

public enum SpaceProcessorType {

	SPACE_CREATE("SPACE_CREATE", "空间创建"),

	SPACE_UPDATE("SPACE_UPDATE", "空间修改"),

	SPACE_RENAME("SPACE_RENAME", "空间重命名"),

	SPACE_DELETE("SPACE_DELETE", "空间删除"),

	SPACE_TEAM_INSERT("SPACE_TEAM_INSERT", "新增团队"),

	SPACE_TEAM_UPDATE("SPACE_TEAM_UPDATE", "编辑团队"),

	SPACE_TEAM_DELETE("SPACE_TEAM_DELETE", "删除团队"),

	SPACE_STAR_INSERT("SPACE_STAR_INSERT", "新增星标"),

	SPACE_STAR_UPDATE("SPACE_STAR_UPDATE", "编辑星标"),

	SPACE_STAR_DELETE("SPACE_STAR_DELETE", "删除星标"),

	;

	public static List<SpaceProcessorType> getList() {
		List<SpaceProcessorType> list = new ArrayList<SpaceProcessorType>();
		for (SpaceProcessorType type : SpaceProcessorType.values()) {
			list.add(type);
		}
		return list;
	}

	private SpaceProcessorType(String value, String text) {
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

	public static SpaceProcessorType get(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		SpaceProcessorType type = null;
		for (SpaceProcessorType one : SpaceProcessorType.values()) {
			if (one.getValue().equalsIgnoreCase(value)) {
				type = one;
				break;
			}
		}
		return type;
	}
}
