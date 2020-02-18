package com.teamide.ide.generater.sql;

import com.teamide.app.process.dao.sql.Delete;
import com.teamide.util.StringUtil;

public class DeleteGenerater extends SqlGenerater {

	protected final Delete delete;

	public DeleteGenerater(Delete delete) {
		super(delete);
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

		content.append("\n");

		return content;
	}

}
