package com.teamide.ide.protect.processor.repository.generater.dao;

import java.util.ArrayList;
import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.app.process.dao.sql.CustomSql;
import com.teamide.app.process.dao.sql.SqlTemplate;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

public abstract class SQLDaoGenerater extends BaseDaoGenerater {

	public SQLDaoGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
	}

	public void buildSQLData() {
		DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
		List<SqlTemplate> templates = new ArrayList<SqlTemplate>();
		data.put("$sqlType", sqlProcess.getSqlType());

		data.put("$result_classname", "Map<String, Object>");
		if (sqlProcess.getSqlType().startsWith("SELECT")) {

			templates.clear();
			sqlProcess.getSelect().appendSelectSql(templates);
			data.put("$select", formatSqlTemplate(templates));
			templates.clear();
			sqlProcess.getSelect().appendSelectFromSql(templates);
			data.put("$from", formatSqlTemplate(templates));
			templates.clear();
			sqlProcess.getSelect().appendWhereSql(templates);
			data.put("$where", formatSqlTemplate(templates));
			templates.clear();
			sqlProcess.getSelect().appendWhereAfterSql(templates);
			data.put("$whereAfter", formatSqlTemplate(templates));
			templates.clear();
			if (sqlProcess.getSqlType().indexOf("PAGE") >= 0) {
				data.put("$result_classname", "PageResultBean<Map<String, Object>>");
			} else if (sqlProcess.getSqlType().indexOf("LIST") >= 0) {
				data.put("$result_classname", "List<Map<String, Object>>");
			}

		} else if (sqlProcess.getSqlType().startsWith("INSERT")) {
			templates.clear();
			sqlProcess.getInsert().appendSql(templates);
			data.put("$before", formatSqlTemplate(templates));
			templates.clear();
			sqlProcess.getInsert().appendColumnSql(templates);
			data.put("$column", formatSqlTemplate(templates));
			templates.clear();

		} else if (sqlProcess.getSqlType().startsWith("UPDATE")) {
			templates.clear();
			sqlProcess.getUpdate().appendSql(templates);
			data.put("$before", formatSqlTemplate(templates));
			templates.clear();
			sqlProcess.getUpdate().appendSetSql(templates);
			data.put("$set", formatSqlTemplate(templates));
			templates.clear();
			sqlProcess.getUpdate().appendWhereSql(templates);
			data.put("$where", formatSqlTemplate(templates));
			templates.clear();
		} else if (sqlProcess.getSqlType().startsWith("DELETE")) {
			templates.clear();
			sqlProcess.getDelete().appendSql(templates);
			data.put("$before", formatSqlTemplate(templates));
			templates.clear();
			sqlProcess.getDelete().appendWhereSql(templates);
			data.put("$where", formatSqlTemplate(templates));
			templates.clear();
		} else if (sqlProcess.getSqlType().startsWith("CUSTOM")) {
			CustomSql customSql = sqlProcess.getCustomSql();
			if (StringUtil.isEmpty(customSql.getCustomsqltype()) && !StringUtil.isEmpty(customSql.getSql())) {
				if (customSql.getSql().toUpperCase().trim().startsWith("SELECT")) {
					customSql.setCustomsqltype("SELECT_LIST");
				} else if (customSql.getSql().toUpperCase().trim().startsWith("SHOW")) {
					customSql.setCustomsqltype("SELECT_LIST");
				}
			}
			if (StringUtil.isEmpty(customSql.getCustomsqltype())) {
				customSql.setCustomsqltype(null);
			}
			data.put("$customsqltype", sqlProcess.getCustomSql().getCustomsqltype());
			templates.clear();
			sqlProcess.getCustomSql().appendSql(templates);
			data.put("$sql", formatSqlTemplate(templates));
			templates.clear();
			sqlProcess.getCustomSql().appendCountSql(templates);
			data.put("$countSql", formatSqlTemplate(templates));
			templates.clear();

			if (StringUtil.isNotEmpty(sqlProcess.getCustomSql().getCustomsqltype())) {
				if (sqlProcess.getCustomSql().getCustomsqltype().indexOf("PAGE") >= 0) {
					data.put("$result_classname", "PageResultBean<Map<String, Object>>");
				} else if (sqlProcess.getCustomSql().getCustomsqltype().indexOf("LIST") >= 0) {
					data.put("$result_classname", "List<Map<String, Object>>");
				}
			}
		}

	}

	public List<SqlTemplate> formatSqlTemplate(List<SqlTemplate> templates) {
		List<SqlTemplate> list = new ArrayList<SqlTemplate>();
		StringBuffer sql = new StringBuffer();
		for (int i = 0; i < templates.size(); i++) {
			SqlTemplate template = templates.get(i);
			SqlTemplate nextTemplate = null;
			if ((i + 1) < templates.size()) {
				nextTemplate = templates.get(i + 1);

			}
			if (template.isNewline()) {
				if (sql.length() > 0) {
					list.add(new SqlTemplate(sql.toString()));
					sql.setLength(0);
				}
				continue;
			}
			if (StringUtil.isEmpty(template.sql) || !StringUtil.isEmpty(template.condition)
					|| template.getParam() != null) {
				if (sql.length() > 0) {
					list.add(new SqlTemplate(sql.toString()));
					sql.setLength(0);
				}
				list.add(template);
			} else {
				sql.append(template.sql);
			}
			if (sql.length() >= 80) {
				if (nextTemplate != null && !StringUtil.isEmpty(nextTemplate.sql)) {
					if (nextTemplate.sql.length() < 5) {
						continue;
					}
				}
				list.add(new SqlTemplate(sql.toString()));
				sql.setLength(0);
			}
		}
		if (sql.length() > 0) {
			list.add(new SqlTemplate(sql.toString()));
		}
		return list;

	}
}
