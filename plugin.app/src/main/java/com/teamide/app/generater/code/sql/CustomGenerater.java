package com.teamide.app.generater.code.sql;

import com.teamide.app.AppContext;
import com.teamide.app.process.dao.sql.CustomSql;
import com.teamide.util.StringUtil;

public class CustomGenerater extends SqlGenerater {

	protected final CustomSql custom;

	public CustomGenerater(AppContext context, CustomSql custom) {
		super(context, custom);
		this.custom = custom;
	}

	public void doGenerate(int tab) {

		if (StringUtil.isEmpty(custom.getSql())) {
			return;
		}
		String[] sqls = custom.getSql().split("\n");

		for (String sql : sqls) {
			String contentSql = formatCustomToContentSql(sql, tab);
			String mapperSql = formatCustomToMapperSql(sql);

			content.append(getTab(tab)).append("sql.append(\"" + contentSql + "\");").append("\n");

			content_mapper.append(getTab(tab)).append(mapperSql).append("\n");
		}

		if (custom.getCustomsqltype() != null && custom.getCustomsqltype().indexOf("PAGE") >= 0) {
			if (!StringUtil.isEmpty(custom.getCountsql())) {
				String[] countsqls = custom.getCountsql().split("\n");

				for (String countsql : countsqls) {
					String contentSql = formatCustomToContentSql(countsql, tab);
					String mapperSql = formatCustomToMapperSql(countsql);
					content.append(getTab(tab)).append("countSql.append(\"" + contentSql + "\");").append("\n");

					content_count_mapper.append(getTab(tab)).append(mapperSql).append("\n");
				}

			}
		}

	}

}
