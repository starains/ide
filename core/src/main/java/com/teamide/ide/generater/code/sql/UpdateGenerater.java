package com.teamide.ide.generater.code.sql;

import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.process.dao.sql.Update;
import com.teamide.app.process.dao.sql.UpdateColumn;
import com.teamide.app.variable.VariableBean;
import com.teamide.app.variable.VariableValidate;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public class UpdateGenerater extends SqlGenerater {

	protected final Update update;

	public UpdateGenerater(AppContext context, String factory_classname, Update update) {
		super(context, factory_classname, update);
		this.update = update;
	}

	public void doGenerate(int tab) {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE  ");

		String table = StringUtil.trim(update.getTable());
		if (!StringUtil.isEmpty(table)) {
			sql.append(table);
			sql.append(" ");
		}

		sql.append("SET ");

		content.append(getTab(tab)).append("// 组合更新语句").append("\n");
		content.append(getTab(tab)).append("sql.append(\"" + sql + "\");").append("\n");
		content_mapper.append(getTab(tab)).append(sql).append("\n");

		content.append("\n");
		content.append(getTab(tab)).append("// 组合更新字段").append("\n");

		content_mapper.append(getTab(tab - 1)).append("<trim suffixOverrides=\",\">").append("\n");
		appendColumn(tab);
		content_mapper.append(getTab(tab - 1)).append("</trim>").append("\n");

		content.append("\n");
		content.append(getTab(tab)).append("// 组合条件语句").append("\n");
		ignoreWhereTablealias(update.getWheres());
		appendWhere(tab, update.getWheres());

		if (update.getAppends() != null && update.getAppends().size() > 0) {
			content.append("\n");
			content.append(getTab(tab)).append("// 追加SQL").append("\n");
			appendAppends(tab, update.getAppends());
		}

	}

	public void appendColumn(int tab) {

		List<UpdateColumn> columns = update.getColumns();

		if (columns == null || columns.size() == 0) {
			return;
		}
		for (UpdateColumn column : columns) {

			int t = tab;
			if (StringUtil.isNotTrimEmpty(column.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(ObjectUtil.isTrue(" + factory_classname + ".getValueByJexlScript(\""
						+ column.getIfrule() + "\", data))) {").append("\n");

				content_mapper.append(getTab(tab));
				content_mapper.append("<if test=\"" + getFormatIfrule(column.getIfrule()) + "\" >").append("\n");
				t++;
			} else {
			}
			appendColumnSql(t, column);

			if (StringUtil.isNotTrimEmpty(column.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				content_mapper.append(getTab(tab)).append("</if>").append("\n");
			}

		}

	}

	public void appendColumnSql(int tab, UpdateColumn column) {
		if (ObjectUtil.isTrue(column.getCustom())) {
			String customsql = column.getCustomsql();
			content.append(getTab(tab)).append("sql.append(\"" + customsql + "\");").append("\n");

			content_mapper.append(getTab(tab)).append(getFormatSql(customsql)).append("\n");
			return;
		}

		String name = StringUtil.trim(column.getName());

		String placeKey = base.getPlaceKey(name, keyCache);
		placeKey = StringUtil.trim(placeKey);

		if (StringUtil.isNotTrimEmpty(column.getValuer()) || StringUtil.isNotTrimEmpty(column.getValue())
				|| StringUtil.isNotTrimEmpty(column.getDefaultvalue())) {

			VariableBean variable = new VariableBean();
			variable.setValuer(column.getValuer());
			variable.setValue(column.getValue());
			variable.setDefaultvalue(column.getDefaultvalue());
			variable.setName(placeKey);
			variables.add(variable);
		} else {
			if (!placeKey.equals(name)) {
				VariableBean variable = new VariableBean();
				variable.setValue(name);
				variable.setName(placeKey);
				variables.add(variable);
			}
		}
		if (ObjectUtil.isTrue(column.getRequired())) {
			VariableValidate validate = new VariableValidate();
			validate.setRequired(true);
			validate.setValue(placeKey);
			validates.add(validate);
		}

		if (StringUtil.isNotTrimEmpty(column.getValuer())) {

			content.append(getTab(tab));
			content.append("value = new " + column.getValuer() + "().getValue();").append("\n");
		} else {
			if (StringUtil.isNotTrimEmpty(column.getValue())) {
				content.append(getTab(tab));
				content.append(
						"value = " + factory_classname + ".getValueByJexlScript(\"" + column.getValue() + "\", data);")
						.append("\n");
			} else {
				content.append(getTab(tab));
				content.append("value = data.get(\"" + column.getName() + "\");").append("\n");

				if (StringUtil.isNotTrimEmpty(column.getDefaultvalue())) {
					content.append(getTab(tab));
					content.append("if(value == null || StringUtil.isEmptyIfStr(value)) {").append("\n");

					content.append(getTab(tab + 1));
					content.append("value = " + factory_classname + ".getValueByJexlScript(\""
							+ column.getDefaultvalue() + "\", data);").append("\n");

					content.append(getTab(tab)).append("}").append("\n");
				}

			}
		}

		content.append(getTab(tab));
		content.append("if(value != null) {").append("\n");

		if (!ObjectUtil.isTrue(column.getRequired())) {
			content_mapper.append(getTab(tab));
			content_mapper.append("<if test=\"" + placeKey + " != null\">").append("\n");

		}

		StringBuffer sql = new StringBuffer();
		sql.append(name).append("=").append(":" + placeKey).append(",");

		content.append(getTab(tab + 1));
		content.append("setSql.append(\"" + sql + "\");").append("\n");

		content_mapper.append(getTab(tab + 1));
		content_mapper.append(name).append("=").append("#{" + placeKey + "}").append(",").append("\n");

		content.append(getTab(tab + 1));
		content.append("param.put(\"" + placeKey + "\", value);").append("\n");

		if (ObjectUtil.isTrue(column.getRequired())) {
			content.append(getTab(tab)).append("} else {").append("\n");

			content.append(getTab(tab + 1))
					.append("throw new FieldValidateException(\"" + column.getName() + " value is required.\");")
					.append("\n");

		}

		content.append(getTab(tab)).append("}").append("\n");

		if (!ObjectUtil.isTrue(column.getRequired())) {
			content_mapper.append(getTab(tab));
			content_mapper.append("</if>").append("\n");
		}

	}
}
