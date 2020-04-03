package com.teamide.app.generater.code;

public class CodeGenerater {
	protected final StringBuffer content = new StringBuffer();
	protected final String getDataName;
	protected final String setDataName;

	public CodeGenerater(String getDataName, String setDataName) {
		this.getDataName = getDataName;
		this.setDataName = setDataName;
	}

	public String getTab(int tab) {
		String res = "";
		for (int i = 0; i < tab; i++) {
			res += "\t";
		}
		return res;
	}

	public StringBuffer getContent() {
		return content;
	}
}
