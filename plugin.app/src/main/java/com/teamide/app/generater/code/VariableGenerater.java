package com.teamide.app.generater.code;

import java.util.List;

import com.teamide.app.variable.Variable;
import com.teamide.util.StringUtil;

public class VariableGenerater extends CodeGenerater {

	public VariableGenerater() {
		super("variableCache", "variableCache");
	}

	public VariableGenerater(String getDataName, String setDataName) {
		super(getDataName, setDataName);
	}

	public StringBuffer generate(int tab, List<Variable> variables) {
		if (variables == null || variables.size() == 0) {
			return content;
		}
		content.append(getTab(tab)).append("// 定义变量").append("\n");
		for (Variable variable : variables) {
			appendVariable(tab, variable);
		}

		return content;
	}

	public void appendVariable(int tab, Variable variable) {
		if (StringUtil.isNotTrimEmpty(variable.getValuer())) {

			content.append(getTab(tab));
			content.append("value = new " + variable.getValuer() + "().getValue();").append("\n");
		} else {
			if (StringUtil.isNotTrimEmpty(variable.getValue())) {
				content.append(getTab(tab));
				content.append(
						"value = factory.getValueByJexlScript(\"" + variable.getValue() + "\", " + getDataName + ");")
						.append("\n");
			} else {
				content.append(getTab(tab));
				content.append(
						"value = factory.getValueByJexlScript(\"" + variable.getName() + "\", " + getDataName + ");")
						.append("\n");

				if (StringUtil.isNotTrimEmpty(variable.getDefaultvalue())) {
					content.append(getTab(tab));
					content.append("if(value == null || StringUtil.isEmptyIfStr(value)) {").append("\n");

					content.append(getTab(tab + 1));
					content.append("value = factory.getValueByJexlScript(\"" + variable.getDefaultvalue() + "\", "
							+ getDataName + ");").append("\n");

					content.append(getTab(tab)).append("}").append("\n");
				}

			}
		}

		content.append(getTab(tab));
		content.append("" + setDataName + ".put(\"" + variable.getName() + "\", value);").append("\n");

	}
}
