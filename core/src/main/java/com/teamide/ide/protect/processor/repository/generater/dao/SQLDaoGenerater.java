package com.teamide.ide.protect.processor.repository.generater.dao;

import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.app.process.dao.sql.CustomSql;
import com.teamide.ide.generater.sql.CustomGenerater;
import com.teamide.ide.generater.sql.DeleteGenerater;
import com.teamide.ide.generater.sql.InsertGenerater;
import com.teamide.ide.generater.sql.SelectGenerater;
import com.teamide.ide.generater.sql.UpdateGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

public abstract class SQLDaoGenerater extends BaseDaoGenerater {

	public SQLDaoGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
	}

	public void buildSQLData() {
		DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
		data.put("$sqlType", sqlProcess.getSqlType());

		data.put("$result_classname", "Map<String, Object>");
		if (sqlProcess.getSqlType().indexOf("SELECT") >= 0) {

			SelectGenerater selectGenerater = new SelectGenerater(sqlProcess.getSelect());
			data.put("$content", selectGenerater.generate(2));

			if (sqlProcess.getSqlType().indexOf("PAGE") >= 0) {
				data.put("$result_classname", "PageResultBean<Map<String, Object>>");
			} else if (sqlProcess.getSqlType().indexOf("LIST") >= 0) {
				data.put("$result_classname", "List<Map<String, Object>>");
			}

		} else if (sqlProcess.getSqlType().indexOf("INSERT") >= 0) {

			InsertGenerater insertGenerater = new InsertGenerater(sqlProcess.getInsert());
			data.put("$content", insertGenerater.generate(2));

		} else if (sqlProcess.getSqlType().indexOf("UPDATE") >= 0) {

			UpdateGenerater updateGenerater = new UpdateGenerater(sqlProcess.getUpdate());
			data.put("$content", updateGenerater.generate(2));

		} else if (sqlProcess.getSqlType().indexOf("DELETE") >= 0) {

			DeleteGenerater deleteGenerater = new DeleteGenerater(sqlProcess.getDelete());
			data.put("$content", deleteGenerater.generate(2));

		} else if (sqlProcess.getSqlType().indexOf("CUSTOM") >= 0) {
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

			data.put("$customsqltype", customSql.getCustomsqltype());

			CustomGenerater customGenerater = new CustomGenerater(sqlProcess.getCustomSql());
			data.put("$content", customGenerater.generate(2));

			if (StringUtil.isNotEmpty(sqlProcess.getCustomSql().getCustomsqltype())) {
				if (sqlProcess.getCustomSql().getCustomsqltype().indexOf("PAGE") >= 0) {
					data.put("$result_classname", "PageResultBean<Map<String, Object>>");
				} else if (sqlProcess.getCustomSql().getCustomsqltype().indexOf("LIST") >= 0) {
					data.put("$result_classname", "List<Map<String, Object>>");
				}
			}
		} else {
			System.out.println(JSONObject.toJSONString(dao));
		}

	}
}
