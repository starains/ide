package com.teamide.ide.generater.code.sql;

import java.util.List;

import com.teamide.app.process.dao.sql.Insert;
import com.teamide.app.process.dao.sql.InsertColumn;
import com.teamide.bean.VariableBean;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;
import com.teamide.variable.VariableValidate;

public class InsertGenerater extends SqlGenerater {

	protected final Insert insert;

	public InsertGenerater(String factory_classname, Insert insert) {
		super(factory_classname, insert);
		this.insert = insert;
	}

	private final StringBuffer columnSql = new StringBuffer();

	private final StringBuffer valueSql = new StringBuffer();

	public void doGenerate(int tab) {
		columnSql.setLength(0);
		valueSql.setLength(0);
		StringBuffer sql = new StringBuffer();

		sql.append("INSERT INTO  ");

		String table = StringUtil.trim(insert.getTable());
		if (!StringUtil.isEmpty(table)) {
			sql.append(table);
			sql.append(" ");
		}

		content.append(getTab(tab)).append("// 组合新增语句").append("\n");
		content.append(getTab(tab)).append("sql.append(\"" + sql + "\");").append("\n");

		content_mapper.append(getTab(tab)).append(sql).append("\n");

		content.append("\n");
		content.append(getTab(tab)).append("// 组合新增字段").append("\n");
		appendColumn(tab);

		content_mapper.append("\n");
		content_mapper.append(getTab(tab)).append("(").append("\n");

		content_mapper.append(getTab(tab - 1)).append("<trim suffixOverrides=\",\">").append("\n");
		content_mapper.append(columnSql).append("\n");
		content_mapper.append(getTab(tab - 1)).append("</trim>").append("\n");
		content_mapper.append(getTab(tab)).append(") VALUES (").append("\n");
		content_mapper.append(getTab(tab - 1)).append("<trim suffixOverrides=\",\">").append("\n");
		content_mapper.append(valueSql).append("\n");
		content_mapper.append(getTab(tab - 1)).append("</trim>").append("\n");
		content_mapper.append(getTab(tab)).append(")").append("\n");

		if (insert.getAppends() != null && insert.getAppends().size() > 0) {
			content.append("\n");
			content.append(getTab(tab)).append("// 追加SQL").append("\n");
			appendAppends(tab, insert.getAppends());
		}
	}

	public void appendColumn(int tab) {

		List<InsertColumn> columns = insert.getColumns();

		if (columns == null || columns.size() == 0) {
			return;
		}
		for (InsertColumn column : columns) {

			int t = tab;
			if (StringUtil.isNotTrimEmpty(column.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(ObjectUtil.isTrue(" + factory_classname + ".getValueByJexlScript(\""
						+ column.getIfrule() + "\", data))) {").append("\n");

				columnSql.append(getTab(tab));
				columnSql.append("<if test=\"" + getFormatIfrule(column.getIfrule()) + "\" >").append("\n");

				valueSql.append(getTab(tab));
				valueSql.append("<if test=\"" + getFormatIfrule(column.getIfrule()) + "\" >").append("\n");
				t++;
			} else {
			}
			appendColumnSql(t, column);

			if (StringUtil.isNotTrimEmpty(column.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				columnSql.append(getTab(tab)).append("</if>").append("\n");
				valueSql.append(getTab(tab)).append("</if>").append("\n");
			}

		}

	}

	public void appendColumnSql(int tab, InsertColumn column) {
		if (ObjectUtil.isTrue(column.getCustom())) {
			String customsql = column.getCustomsql();
			content.append(getTab(tab)).append("sql.append(\"" + customsql + "\");").append("\n");

			content_mapper.append(getTab(tab)).append(getFormatSql(customsql)).append("\n");

			return;
		}
		String name = StringUtil.trim(column.getName());
		if (ObjectUtil.isTrue(column.getAutoincrement())) {
			if (!StringUtil.isEmpty(name)) {
				content.append(getTab(tab));
				content.append("columnSql.append(\"" + name + ",\");").append("\n");
				content.append(getTab(tab));
				content.append("valueSql.append(\"null,\");").append("\n");

				columnSql.append(getTab(tab)).append(name + ",").append("\n");
				valueSql.append(getTab(tab)).append("null,").append("\n");

			}
			return;
		}

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
			columnSql.append(getTab(tab));
			columnSql.append("<if test=\"" + placeKey + " != null\">").append("\n");
			valueSql.append(getTab(tab));
			valueSql.append("<if test=\"" + placeKey + " != null\">").append("\n");
		}

		content.append(getTab(tab + 1));
		content.append("columnSql.append(\"" + name + ",\");").append("\n");
		content.append(getTab(tab + 1));
		content.append("valueSql.append(\":" + placeKey + ",\");").append("\n");

		columnSql.append(getTab(tab)).append(name + ",").append("\n");
		valueSql.append(getTab(tab)).append("#{" + placeKey + "},").append("\n");

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
			columnSql.append(getTab(tab));
			columnSql.append("</if>").append("\n");
			valueSql.append(getTab(tab));
			valueSql.append("</if>").append("\n");
		}

	}
}
