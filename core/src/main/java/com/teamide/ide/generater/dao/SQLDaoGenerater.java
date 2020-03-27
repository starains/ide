package com.teamide.ide.generater.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.teamide.app.process.dao.sql.SelectColumn;
import com.teamide.app.process.dao.sql.SubSelect;
import com.teamide.app.process.dao.sql.Update;
import com.teamide.ide.generater.code.ValidateGenerater;
import com.teamide.ide.generater.code.VariableGenerater;
import com.teamide.ide.generater.code.sql.CustomGenerater;
import com.teamide.ide.generater.code.sql.DeleteGenerater;
import com.teamide.ide.generater.code.sql.InsertGenerater;
import com.teamide.ide.generater.code.sql.SelectGenerater;
import com.teamide.ide.generater.code.sql.SqlGenerater;
import com.teamide.ide.generater.code.sql.UpdateGenerater;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;
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
		if (sqlProcess.getSqlType().indexOf("SELECT_COUNT") >= 0) {
			sqlProcess.setSqlType("SELECT_ONE");
		}
		data.put("$sqlType", sqlProcess.getSqlType());

		data.put("$build_name", "buildSqlParam");
		data.put("$format_name", "format");
		data.put("$query_name", "query");

		data.put("$result_classname", "Map<String, Object>");
		data.put("$database", null);
		data.put("$is_batch", false);
		data.put("$subselects", null);
		if (sqlProcess.getSqlType().indexOf("BATCH") >= 0) {
			data.put("$is_batch", true);
			data.put("$result_classname", "List<Map<String, Object>>");
		}
		String datarule = null;
		SqlGenerater sqlGenerater = null;
		if (sqlProcess.getSqlType().indexOf("SELECT") >= 0) {
			datarule = sqlProcess.getSelect().getData();
			data.put("$database", getDatabase(sqlProcess.getSelect()));

			sqlGenerater = new SelectGenerater(context, getAppFactoryClassname(), sqlProcess.getSelect());
			sqlGenerater.generate(2);
			data.put("$content", sqlGenerater.getContent());

			data.put("$content_mapper", sqlGenerater.getContentMapper());

			data.put("$content_count_mapper", sqlGenerater.getContentCountMapper());

			JSONArray formats = new JSONArray();
			Select select = sqlProcess.getSelect();
			if (select.getColumns() != null) {
				for (SelectColumn selectColumn : select.getColumns()) {
					String key = selectColumn.getAlias();
					if (StringUtil.isEmpty(key)) {
						key = selectColumn.getName();
					}
					if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(selectColumn.getFormatvalue())) {
						JSONObject format = (JSONObject) JSON.toJSON(selectColumn);
						format.put("$key", key);
						formats.add(format);
					}
				}
			}
			data.put("$formats", formats);
			if (sqlProcess.getSqlType().indexOf("PAGE") >= 0) {
				data.put("$result_classname", "PageResultBean<Map<String, Object>>");
			} else if (sqlProcess.getSqlType().indexOf("LIST") >= 0) {
				data.put("$result_classname", "List<Map<String, Object>>");
			}

			if (sqlProcess.getSelect().getSubselects() != null && sqlProcess.getSelect().getSubselects().size() > 0) {
				JSONArray $subselects = new JSONArray();

				for (SubSelect subSelect : sqlProcess.getSelect().getSubselects()) {
					if (subSelect.getSelect() != null) {
						String $name = subSelect.getName();
						if (StringUtil.isTrimEmpty($name)) {
							continue;
						}
						$name = $name.trim();
						JSONObject $subselect = (JSONObject) JSON.toJSON(subSelect);
						$subselects.add($subselect);
						$subselect.put("$database", getDatabase(subSelect.getSelect()));
						$subselect.put("$name", $name);
						$subselect.put("$method", $name.substring(0, 1).toUpperCase() + $name.substring(1));
						sqlGenerater = new SelectGenerater(context, getAppFactoryClassname(), subSelect.getSelect());
						sqlGenerater.generate(2);

						$subselect.put("$content", sqlGenerater.getContent());
					}
				}
				data.put("$subselects", $subselects);
			}

		} else if (sqlProcess.getSqlType().indexOf("INSERT") >= 0) {
			datarule = sqlProcess.getInsert().getData();
			data.put("$database", getDatabase(sqlProcess.getInsert()));

			sqlGenerater = new InsertGenerater(context, getAppFactoryClassname(), sqlProcess.getInsert());
			sqlGenerater.generate(2);
			data.put("$content", sqlGenerater.getContent());

			data.put("$content_mapper", sqlGenerater.getContentMapper());

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

			sqlGenerater = new UpdateGenerater(context, getAppFactoryClassname(), sqlProcess.getUpdate());
			sqlGenerater.generate(2);
			data.put("$content", sqlGenerater.getContent());

			data.put("$content_mapper", sqlGenerater.getContentMapper());

		} else if (sqlProcess.getSqlType().indexOf("DELETE") >= 0) {
			datarule = sqlProcess.getDelete().getData();
			data.put("$database", getDatabase(sqlProcess.getDelete()));

			sqlGenerater = new DeleteGenerater(context, getAppFactoryClassname(), sqlProcess.getDelete());
			sqlGenerater.generate(2);
			data.put("$content", sqlGenerater.getContent());

			data.put("$content_mapper", sqlGenerater.getContentMapper());

		} else if (sqlProcess.getSqlType().indexOf("CUSTOM") >= 0) {
			data.put("$execute_type", "select");
			datarule = sqlProcess.getCustomSql().getData();
			CustomSql customSql = sqlProcess.getCustomSql();
			data.put("$database", getDatabase(customSql));
			if (StringUtil.isEmpty(customSql.getCustomsqltype()) && !StringUtil.isEmpty(customSql.getSql())) {
				if (customSql.getSql().toUpperCase().trim().startsWith("SELECT")) {
					customSql.setCustomsqltype("SELECT_LIST");
				} else if (customSql.getSql().toUpperCase().trim().startsWith("SHOW")) {
					customSql.setCustomsqltype("SELECT_LIST");
				} else if (customSql.getSql().toUpperCase().trim().startsWith("UPDATE")) {
					data.put("$execute_type", "update");
				} else if (customSql.getSql().toUpperCase().trim().startsWith("DELETE")) {
					data.put("$execute_type", "delete");
				} else if (customSql.getSql().toUpperCase().trim().startsWith("INSERT")) {
					data.put("$execute_type", "insert");
				}
			}
			if (StringUtil.isEmpty(customSql.getCustomsqltype())) {
				customSql.setCustomsqltype(null);
			}

			data.put("$customsqltype", customSql.getCustomsqltype());

			sqlGenerater = new CustomGenerater(context, getAppFactoryClassname(), sqlProcess.getCustomSql());
			sqlGenerater.generate(2);
			data.put("$content", sqlGenerater.getContent());

			data.put("$content_mapper", sqlGenerater.getContentMapper());

			data.put("$content_count_mapper", sqlGenerater.getContentCountMapper());

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

		if (sqlGenerater != null) {
			VariableGenerater variableGenerater = new VariableGenerater(getAppFactoryClassname());
			StringBuffer $variable_content = variableGenerater.generate(2, sqlGenerater.getVariables());
			if (StringUtil.isEmpty($variable_content)) {
				$variable_content = null;
			}
			data.put("$variable_content", $variable_content);

			ValidateGenerater validateGenerater = new ValidateGenerater(getAppFactoryClassname());
			StringBuffer $validate_content = validateGenerater.generate(2, sqlGenerater.getValidates());
			if (StringUtil.isEmpty($validate_content)) {
				$validate_content = null;
			}
			data.put("$validate_content", $validate_content);
		}

		datarule = StringUtil.trim(datarule);
		data.put("$datarule", null);
		if (!StringUtil.isEmpty(datarule)) {
			data.put("$datarule", datarule);
		}
	}
}
