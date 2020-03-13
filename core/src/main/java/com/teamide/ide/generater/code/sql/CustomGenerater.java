package com.teamide.ide.generater.code.sql;

import com.teamide.app.process.dao.sql.CustomSql;
import com.teamide.util.StringUtil;

public class CustomGenerater extends SqlGenerater {

	protected final CustomSql custom;

	public CustomGenerater(String factory_classname, CustomSql custom) {
		super(factory_classname, custom);
		this.custom = custom;
	}

	public StringBuffer generate(int tab) {

		if (StringUtil.isEmpty(custom.getSql())) {
			return content;
		}
		String[] sqls = custom.getSql().split("\n");

		for (String sql : sqls) {
			content.append(getTab(tab)).append("sql.append(\"" + sql + "\");").append("\n");
		}

		if (custom.getCustomsqltype() != null && custom.getCustomsqltype().indexOf("PAGE") >= 0) {
			if (!StringUtil.isEmpty(custom.getCountsql())) {
				String[] countsqls = custom.getCountsql().split("\n");

				for (String countsql : countsqls) {
					content.append(getTab(tab)).append("countSql.append(\"" + countsql + "\");").append("\n");
				}

			}
		}

		return content;
	}

}
