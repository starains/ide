package com.teamide.ide.protect.enums;

public enum UploadStatus {

	NOT_UPLOAD("NOT_UPLOAD", "未上传"),

	UPLOADING("UPLOADING", "上传中"),

	UPLOADED("UPLOADED", "已上传"),

	;

	private UploadStatus(String value, String text) {

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
