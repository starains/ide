package com.teamide.ide.generater.sql;

import java.util.List;

import com.teamide.app.process.dao.sql.Insert;
import com.teamide.app.process.dao.sql.InsertColumn;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public class InsertGenerater extends SqlGenerater {

	protected final Insert insert;

	public InsertGenerater(Insert insert) {
		super(insert);
		this.insert = insert;
	}

	public StringBuffer generate(int tab) {
		StringBuffer sql = new StringBuffer();

		sql.append("INSERT INTO  ");

		String table = StringUtil.trim(insert.getTable());
		if (!StringUtil.isEmpty(table)) {
			sql.append(table);
			sql.append(" ");
		}

		content.append(getTab(tab)).append("// 组合新增语句").append("\n");
		content.append(getTab(tab)).append("sql.append(\"" + sql + "\");").append("\n");

		content.append("\n");
		content.append(getTab(tab)).append("// 组合新增字段").append("\n");
		appendColumn(tab);

		return content;
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
				content.append("if(ObjectUtil.isTrue(JexlTool.invoke(\"" + column.getIfrule() + "\", data))) {")
						.append("\n");
				t++;
			} else {
			}
			appendColumnSql(t, column);

			if (StringUtil.isNotTrimEmpty(column.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");
			}

		}

	}

	public void appendColumnSql(int tab, InsertColumn column) {
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
				content.append("value = JexlTool.invoke(\"" + column.getValue() + "\", data);").append("\n");
			} else {
				content.append(getTab(tab));
				content.append("value = JexlTool.invoke(\"" + column.getName() + "\", data);").append("\n");

				if (StringUtil.isNotTrimEmpty(column.getDefaultvalue())) {
					content.append(getTab(tab));
					content.append("if(value == null || StringUtil.isEmptyIfStr(value)) {").append("\n");

					content.append(getTab(tab + 1));
					content.append("value = JexlTool.invoke(\"" + column.getDefaultvalue() + "\", data);").append("\n");

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
		sql.append(name).append("=").append(":" + placeKey);

		content.append(getTab(tab + 1));
		content.append("columnSql.append(\"" + name + ",\");").append("\n");
		content.append(getTab(tab + 1));
		content.append("valueSql.append(\":" + placeKey + ",\");").append("\n");

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
