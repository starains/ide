package com.teamide.ide.generater;

import java.util.List;

import com.teamide.exception.Errcode;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;
import com.teamide.variable.VariableValidate;

public class ValidateGenerater extends CodeGenerater {

	public ValidateGenerater(String factory_classname) {
		super(factory_classname);
	}

	public StringBuffer generate(int tab, List<VariableValidate> validates) {
		if (validates == null || validates.size() == 0) {
			return content;
		}
		content.append(getTab(tab)).append("// 验证").append("\n");
		for (VariableValidate validate : validates) {
			appendValidate(tab, validate);
		}

		return content;
	}

	public void appendValidate(int tab, VariableValidate validate) {

		if (StringUtil.isNotEmpty(validate.getValidator())) {
			content.append(getTab(tab));
			content.append("new " + validate.getValidator() + "().validate();").append("\n");
		} else {
			String errcode = validate.getErrcode();
			String errmsg = validate.getErrmsg();
			if (StringUtil.isEmpty(errcode)) {
				errcode = Errcode.FIELD_VALIDATE_FAIL;
			}
			if (StringUtil.isEmpty(errmsg)) {
				errmsg = "field validation is fail.";
			}
			if (StringUtil.isNotEmpty(validate.getRule())) {

				content.append(getTab(tab)).append("if(ObjectUtil.isTrue(" + factory_classname
						+ ".getValueByJexlScript(\"" + validate.getRule() + "\", variableCache))) {").append("\n");
				content.append(getTab(tab + 1))
						.append("throw new FieldValidateException(\"" + errcode + "\", \"" + errmsg + "\");")
						.append("\n");
				content.append(getTab(tab)).append("}").append("\n");
			} else {
				content.append(getTab(tab));
				content.append("value = " + factory_classname + ".getValueByJexlScript(\"" + validate.getValue()
						+ "\", variableCache);").append("\n");
				if (ObjectUtil.isTrue(validate.getRequired())) {
					content.append(getTab(tab)).append("if(value == null || StringUtil.isEmptyIfStr(value)) {")
							.append("\n");
					content.append(getTab(tab + 1))
							.append("throw new FieldValidateException(\"" + errcode + "\", \"" + errmsg + "\");")
							.append("\n");
					content.append(getTab(tab)).append("}").append("\n");
				}
				if (StringUtil.isNotEmpty(validate.getType())) {
					String type = validate.getType();

					content.append(getTab(tab))
							.append("if(value != null && !Pattern.matches(com.teamide.variable.enums.ValueType.get(\""
									+ type + "\").getPattern(), String.valueOf(value))) {")
							.append("\n");
					content.append(getTab(tab + 1))
							.append("throw new FieldValidateException(\"" + errcode + "\", \"" + errmsg + "\");")
							.append("\n");
					content.append(getTab(tab)).append("}").append("\n");
				}
				if (StringUtil.isNotEmpty(validate.getPattern())) {
					String pattern = validate.getPattern();

					content.append(getTab(tab)).append(
							"if(value != null && !Pattern.matches(\"" + pattern + "\", String.valueOf(value))) {")
							.append("\n");
					content.append(getTab(tab + 1))
							.append("throw new FieldValidateException(\"" + errcode + "\", \"" + errmsg + "\");")
							.append("\n");
					content.append(getTab(tab)).append("}").append("\n");

				}
			}

		}

	}
}
