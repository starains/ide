package com.teamide.ide.generater.code.sql;

import com.teamide.app.AppContext;
import com.teamide.app.process.dao.sql.CustomSql;
import com.teamide.util.StringUtil;

public class CustomGenerater extends SqlGenerater {

	protected final CustomSql custom;

	public CustomGenerater(AppContext context, String factory_classname, CustomSql custom) {
		super(context, factory_classname, custom);
		this.custom = custom;
	}

	public void doGenerate(int tab) {

		if (StringUtil.isEmpty(custom.getSql())) {
			return;
		}
		String[] sqls = custom.getSql().split("\n");

		for (String sql : sqls) {
			content.append(getTab(tab)).append("sql.append(\"" + sql + "\");").append("\n");

			content_mapper.append(getTab(tab)).append(getFormatSql(sql)).append("\n");
		}

		if (custom.getCustomsqltype() != null && custom.getCustomsqltype().indexOf("PAGE") >= 0) {
			if (!StringUtil.isEmpty(custom.getCountsql())) {
				String[] countsqls = custom.getCountsql().split("\n");

				for (String countsql : countsqls) {
					content.append(getTab(tab)).append("countSql.append(\"" + countsql + "\");").append("\n");

					content_count_mapper.append(getTab(tab)).append(getFormatSql(countsql)).append("\n");
				}

			}
		}

	}

}
