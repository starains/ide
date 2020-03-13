package com.teamide.ide.generater.code;

public class CodeGenerater {
	protected final StringBuffer content = new StringBuffer();

	protected final String factory_classname;

	public CodeGenerater(String factory_classname) {
		this.factory_classname = factory_classname;
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
