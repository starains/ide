package com.teamide.ide.generater;

public class CodeGenerater {
	protected final StringBuffer content = new StringBuffer();

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
