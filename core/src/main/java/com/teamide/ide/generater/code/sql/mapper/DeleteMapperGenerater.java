package com.teamide.ide.generater.code.sql.mapper;

import com.teamide.app.process.dao.sql.Delete;
import com.teamide.util.StringUtil;

public class DeleteMapperGenerater extends SqlMapperGenerater {

	protected final Delete delete;

	public DeleteMapperGenerater(String factory_classname, Delete delete) {
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

		content.append(getTab(tab)).append(sql).append("\n");

		ignoreWhereTablealias(delete.getWheres());
		appendWhere(tab, delete.getWheres());

		if (delete.getAppends() != null && delete.getAppends().size() > 0) {
			content.append("\n");
			appendAppends(tab, delete.getAppends());
		}

		content.append("\n");

		return content;
	}

}
