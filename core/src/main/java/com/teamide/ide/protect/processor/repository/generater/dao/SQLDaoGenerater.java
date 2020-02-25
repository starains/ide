package com.teamide.ide.protect.processor.repository.generater.dao;

import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.bean.DatabaseBean;
import com.teamide.app.bean.TableBean;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.app.process.dao.sql.CustomSql;
import com.teamide.app.process.dao.sql.Delete;
import com.teamide.app.process.dao.sql.Insert;
import com.teamide.app.process.dao.sql.InsertColumn;
import com.teamide.app.process.dao.sql.Select;
import com.teamide.app.process.dao.sql.Update;
import com.teamide.ide.generater.sql.CustomGenerater;
import com.teamide.ide.generater.sql.DeleteGenerater;
import com.teamide.ide.generater.sql.InsertGenerater;
import com.teamide.ide.generater.sql.SelectGenerater;
import com.teamide.ide.generater.sql.UpdateGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public abstract class SQLDaoGenerater extends BaseDaoGenerater {

	public SQLDaoGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
	}

	public DatabaseBean getDatabaseByTable(String table) {
		if (table != null) {
			TableBean tab = context.get(TableBean.class, table);
			if (tab != null && !StringUtil.isEmpty(tab.getDatabasename())) {
				return context.get(DatabaseBean.class, tab.getDatabasename());
			}
		}
		return null;
	}

	public DatabaseBean getDatabase(Select select) {
		if (select != null && select.getFroms() != null && select.getFroms().size() > 0) {
			return getDatabaseByTable(select.getFroms().get(0).getTable());
		}
		return null;
	}

	public DatabaseBean getDatabase(Update update) {
		if (update != null) {
			return getDatabaseByTable(update.getTable());
		}
		return null;
	}

	public DatabaseBean getDatabase(Insert insert) {
		if (insert != null) {
			return getDatabaseByTable(insert.getTable());
		}
		return null;
	}

	public DatabaseBean getDatabase(Delete delete) {
		if (delete != null) {
			return getDatabaseByTable(delete.getTable());
		}
		return null;
	}

	public DatabaseBean getDatabase(CustomSql customSql) {
		if (customSql != null && !StringUtil.isEmpty(customSql.getDatabasename())) {
			return context.get(DatabaseBean.class, customSql.getDatabasename());
		}
		return null;
	}

	public void buildSQLData() {
		DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
		data.put("$sqlType", sqlProcess.getSqlType());

		data.put("$result_classname", "Map<String, Object>");
		data.put("$database", null);
		data.put("$is_batch", false);
		if (sqlProcess.getSqlType().indexOf("BATCH") >= 0) {
			data.put("$is_batch", true);
			data.put("$result_classname", "List<Map<String, Object>>");
		}
		String datarule = null;
		if (sqlProcess.getSqlType().indexOf("SELECT") >= 0) {
			datarule = sqlProcess.getSelect().getData();
			data.put("$database", getDatabase(sqlProcess.getSelect()));

			SelectGenerater selectGenerater = new SelectGenerater(getAppFactoryClassname(), sqlProcess.getSelect());
			data.put("$content", selectGenerater.generate(2));

			if (sqlProcess.getSqlType().indexOf("PAGE") >= 0) {
				data.put("$result_classname", "PageResultBean<Map<String, Object>>");
			} else if (sqlProcess.getSqlType().indexOf("LIST") >= 0) {
				data.put("$result_classname", "List<Map<String, Object>>");
			}

		} else if (sqlProcess.getSqlType().indexOf("INSERT") >= 0) {
			datarule = sqlProcess.getInsert().getData();
			data.put("$database", getDatabase(sqlProcess.getInsert()));

			InsertGenerater insertGenerater = new InsertGenerater(getAppFactoryClassname(), sqlProcess.getInsert());
			data.put("$content", insertGenerater.generate(2));

			String $autoincrement_key = null;
			if (sqlProcess.getInsert() != null && sqlProcess.getInsert().getColumns() != null) {
				for (InsertColumn column : sqlProcess.getInsert().getColumns()) {
					if (column == null || StringUtil.isEmpty(StringUtil.trim(column.getName()))) {
						continue;
					}
					if (ObjectUtil.isTrue(column.getAutoincrement())) {
						$autoincrement_key = StringUtil.trim(column.getName());
					}
				}
			}
			data.put("$autoincrement_key", $autoincrement_key);

		} else if (sqlProcess.getSqlType().indexOf("UPDATE") >= 0) {
			datarule = sqlProcess.getUpdate().getData();
			data.put("$database", getDatabase(sqlProcess.getUpdate()));

			UpdateGenerater updateGenerater = new UpdateGenerater(getAppFactoryClassname(), sqlProcess.getUpdate());
			data.put("$content", updateGenerater.generate(2));

		} else if (sqlProcess.getSqlType().indexOf("DELETE") >= 0) {
			datarule = sqlProcess.getDelete().getData();
			data.put("$database", getDatabase(sqlProcess.getDelete()));

			DeleteGenerater deleteGenerater = new DeleteGenerater(getAppFactoryClassname(), sqlProcess.getDelete());
			data.put("$content", deleteGenerater.generate(2));

		} else if (sqlProcess.getSqlType().indexOf("CUSTOM") >= 0) {
			datarule = sqlProcess.getCustomSql().getData();
			CustomSql customSql = sqlProcess.getCustomSql();
			data.put("$database", getDatabase(customSql));
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

			CustomGenerater customGenerater = new CustomGenerater(getAppFactoryClassname(), sqlProcess.getCustomSql());
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

		datarule = StringUtil.trim(datarule);
		data.put("$datarule", null);
		if (!StringUtil.isEmpty(datarule)) {
			data.put("$datarule", datarule);
		}
	}
}
