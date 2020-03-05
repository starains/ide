package com.teamide.ide.generater.sql;

import java.util.List;

import com.teamide.app.process.dao.sql.Update;
import com.teamide.app.process.dao.sql.UpdateColumn;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public class UpdateGenerater extends SqlGenerater {

	protected final Update update;

	public UpdateGenerater(String factory_classname, Update update) {
		super(factory_classname, update);
		this.update = update;
	}

	public StringBuffer generate(int tab) {
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

		content.append("\n");
		content.append(getTab(tab)).append("// 组合更新字段").append("\n");
		appendColumn(tab);

		content.append("\n");
		content.append(getTab(tab)).append("// 组合条件语句").append("\n");
		ignoreWhereTablealias(update.getWheres());
		appendWhere(tab, update.getWheres());

		if (update.getAppends() != null && update.getAppends().size() > 0) {
			content.append("\n");
			content.append(getTab(tab)).append("// 追加SQL").append("\n");
			appendAppends(tab, update.getAppends());
		}

		return content;
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
				t++;
			} else {
			}
			appendColumnSql(t, column);

			if (StringUtil.isNotTrimEmpty(column.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");
			}

		}

	}

	public void appendColumnSql(int tab, UpdateColumn column) {
		if (ObjectUtil.isTrue(column.getCustom())) {
			String customsql = column.getCustomsql();
			content.append(getTab(tab)).append("sql.append(\"" + customsql + "\");").append("\n");
			return;
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

		String name = StringUtil.trim(column.getName());

		String placeKey = base.getPlaceKey(column.getName());
		placeKey = StringUtil.trim(placeKey);
		StringBuffer sql = new StringBuffer();
		sql.append(name).append("=").append(":" + placeKey).append(",");

		content.append(getTab(tab + 1));
		content.append("setSql.append(\"" + sql + "\");").append("\n");

		content.append(getTab(tab + 1));
		content.append("param.put(\"" + placeKey + "\", value);").append("\n");

		if (ObjectUtil.isTrue(column.getRequired())) {
			content.append(getTab(tab)).append("} else {").append("\n");

			content.append(getTab(tab + 1))
					.append("throw new FieldValidateException(\"" + column.getName() + " value is required.\");")
					.append("\n");

		}

		content.append(getTab(tab)).append("}").append("\n");

	}
}
