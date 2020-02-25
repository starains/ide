package com.teamide.ide.generater.sql;

import com.teamide.app.process.dao.sql.Delete;
import com.teamide.util.StringUtil;

public class DeleteGenerater extends SqlGenerater {

	protected final Delete delete;

	public DeleteGenerater(String factory_classname, Delete delete) {
		super(factory_classname, delete);
		this.delete = delete;
	}

	public StringBuffer generate(int tab) {
		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM ");

		String table = StringUtil.trim(delete.getTable());
		if (!StringUtil.isEmpty(table)) {
			sql.append(table);
			sql.append(" ");
		}

		content.append(getTab(tab)).append("// 删除语句").append("\n");
		content.append(getTab(tab)).append("sql.append(\"" + sql + "\");").append("\n");

		content.append(getTab(tab)).append("// 组合条件语句").append("\n");
		appendWhere(tab, delete.getWheres());

		if (delete.getAppends() != null && delete.getAppends().size() > 0) {
			content.append("\n");
			content.append(getTab(tab)).append("// 追加SQL").append("\n");
			appendAppends(tab, delete.getAppends());
		}

		content.append("\n");

		return content;
	}

}
