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
import com.teamide.ide.generater.code.sql.CustomGenerater;
import com.teamide.ide.generater.code.sql.DeleteGenerater;
import com.teamide.ide.generater.code.sql.InsertGenerater;
import com.teamide.ide.generater.code.sql.SelectGenerater;
import com.teamide.ide.generater.code.sql.SqlGenerater;
import com.teamide.ide.generater.code.sql.UpdateGenerater;
import com.teamide.ide.generater.code.sql.mapper.CustomMapperGenerater;
import com.teamide.ide.generater.code.sql.mapper.DeleteMapperGenerater;
import com.teamide.ide.generater.code.sql.mapper.InsertMapperGenerater;
import com.teamide.ide.generater.code.sql.mapper.SelectMapperGenerater;
import com.teamide.ide.generater.code.sql.mapper.UpdateMapperGenerater;
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
		if (sqlProcess.getSqlType().indexOf("SELECT") >= 0) {
			datarule = sqlProcess.getSelect().getData();
			data.put("$database", getDatabase(sqlProcess.getSelect()));

			SqlGenerater sqlGenerater = new SelectGenerater(getAppFactoryClassname(), sqlProcess.getSelect());
			data.put("$content", sqlGenerater.generate(2));

			sqlGenerater = new SelectMapperGenerater(getAppFactoryClassname(), sqlProcess.getSelect());
			data.put("$content_mapper", sqlGenerater.generate(2));

			SelectMapperGenerater selectMapperGenerater = new SelectMapperGenerater(getAppFactoryClassname(),
					sqlProcess.getSelect());
			data.put("$content_count_mapper", selectMapperGenerater.generateCountSql(2));

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
						SelectGenerater subSlectGenerater = new SelectGenerater(getAppFactoryClassname(),
								subSelect.getSelect());
						$subselect.put("$content", subSlectGenerater.generate(2));
					}
				}
				data.put("$subselects", $subselects);
			}

		} else if (sqlProcess.getSqlType().indexOf("INSERT") >= 0) {
			datarule = sqlProcess.getInsert().getData();
			data.put("$database", getDatabase(sqlProcess.getInsert()));

			SqlGenerater sqlGenerater = new InsertGenerater(getAppFactoryClassname(), sqlProcess.getInsert());
			data.put("$content", sqlGenerater.generate(2));

			sqlGenerater = new InsertMapperGenerater(getAppFactoryClassname(), sqlProcess.getInsert());
			data.put("$content_mapper", sqlGenerater.generate(2));

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

			SqlGenerater sqlGenerater = new UpdateGenerater(getAppFactoryClassname(), sqlProcess.getUpdate());
			data.put("$content", sqlGenerater.generate(2));

			sqlGenerater = new UpdateMapperGenerater(getAppFactoryClassname(), sqlProcess.getUpdate());
			data.put("$content_mapper", sqlGenerater.generate(2));

		} else if (sqlProcess.getSqlType().indexOf("DELETE") >= 0) {
			datarule = sqlProcess.getDelete().getData();
			data.put("$database", getDatabase(sqlProcess.getDelete()));

			SqlGenerater sqlGenerater = new DeleteGenerater(getAppFactoryClassname(), sqlProcess.getDelete());
			data.put("$content", sqlGenerater.generate(2));

			sqlGenerater = new DeleteMapperGenerater(getAppFactoryClassname(), sqlProcess.getDelete());
			data.put("$content_mapper", sqlGenerater.generate(2));

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

			SqlGenerater sqlGenerater = new CustomGenerater(getAppFactoryClassname(), sqlProcess.getCustomSql());
			data.put("$content", sqlGenerater.generate(2));

			sqlGenerater = new CustomMapperGenerater(getAppFactoryClassname(), sqlProcess.getCustomSql());
			data.put("$content_mapper", sqlGenerater.generate(2));

			CustomMapperGenerater customMapperGenerater = new CustomMapperGenerater(getAppFactoryClassname(),
					sqlProcess.getCustomSql());
			data.put("$content_count_mapper", customMapperGenerater.generateCountSql(2));

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
