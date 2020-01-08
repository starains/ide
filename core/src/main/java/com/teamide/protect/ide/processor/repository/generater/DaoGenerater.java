package com.teamide.protect.ide.processor.repository.generater;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.enums.DaoProcessType;
import com.teamide.app.process.DaoProcess;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.app.process.dao.sql.CustomSql;
import com.teamide.app.process.dao.sql.From;
import com.teamide.app.process.dao.sql.Select;
import com.teamide.app.process.dao.sql.SelectColumn;
import com.teamide.app.process.dao.sql.Where;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.project.AppBean;

public class DaoGenerater extends CodeGenerater {

	protected final DaoBean dao;

	public DaoGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
		this.dao = dao;
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		String pack = app.getOption().getDaopackage();
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".dao";
		}
		return pack;
	}

	@Override
	public void buildData() {
		DaoProcess daoProcess = dao.getProcess();

		if (DaoProcessType.SQL.getValue().equals(daoProcess.getType())) {
			buildSQLData();
		}

	}

	public void buildSQLData() {
		DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
		JSONArray $list = new JSONArray();
		data.put("$list", $list);
		if (sqlProcess.getSqlType().startsWith("CUSTOM_SQL")) {
			buildCustomSqlData($list, sqlProcess.getCustomSql());
		} else if (sqlProcess.getSqlType().startsWith("SELECT")) {
			buildSelectSqlData($list, sqlProcess.getSelect());
		}

	}

	public void buildSelectSqlData(JSONArray $list, Select select) {
		if (select == null) {
			return;
		}
		JSONObject $select = new JSONObject();
		StringBuffer selectBuffer = new StringBuffer();
		selectBuffer.append("SELECT ");
		$select.put("sql", selectBuffer);
		$list.add($select);
		List<SelectColumn> columns = select.getColumns();
		if (columns == null || columns.size() == 0) {
			selectBuffer.append("* ");
		} else {
			boolean isFirst = true;
			for (SelectColumn column : columns) {
				StringBuffer selectColumnBuffer = new StringBuffer();
				if (ObjectUtil.isTrue(column.getCustom())) {
					selectColumnBuffer.append(column.getCustomsql());
				} else {
					if (StringUtil.isNotEmpty(column.getTablealias())) {
						selectColumnBuffer.append(column.getTablealias() + ".");
					}
					if (StringUtil.isNotEmpty(column.getName())) {
						selectColumnBuffer.append(column.getName());
					}
				}

				if (StringUtil.isNotEmpty(column.getAlias())) {
					selectColumnBuffer.append(" AS " + column.getAlias());
				}

				if (StringUtil.isEmpty(column.getIfrule())) {
					if (!isFirst) {
						selectBuffer.append(", ");
					}
					isFirst = false;
					selectBuffer.append(selectColumnBuffer);
				} else {
					JSONObject $ifrule = new JSONObject();
					$ifrule.put("condition", column.getIfrule());
					$ifrule.put("sql", selectColumnBuffer);
					$list.add($ifrule);
				}

			}
		}

		List<From> froms = select.getFroms();
		if (froms != null && froms.size() > 0) {
			JSONObject $from = new JSONObject();
			StringBuffer fromBuffer = new StringBuffer();
			fromBuffer.append(" FROM");
			$from.put("sql", fromBuffer);
			$list.add($from);
			for (From from : froms) {
				if (StringUtil.isNotEmpty(from.getTable())) {
					fromBuffer.append(" " + from.getTable());
				}
				if (StringUtil.isNotEmpty(from.getAlias())) {
					fromBuffer.append(" AS " + from.getAlias());
				}
			}
		}
		List<Where> wheres = select.getWheres();
		if (wheres != null && wheres.size() > 0) {
			JSONObject $where = new JSONObject();
			StringBuffer whereBuffer = new StringBuffer();
			whereBuffer.append(" WHERE 1=1 ");
			$where.put("sql", whereBuffer);
			$list.add($where);
			for (Where where : wheres) {
				StringBuffer whereColumnBuffer = new StringBuffer();
				whereColumnBuffer.append(where.getRelationaloperation());
				whereColumnBuffer.append(" ");
				if (ObjectUtil.isTrue(where.getCustom())) {
					whereColumnBuffer.append(where.getCustomsql());
				} else {
					if (StringUtil.isNotEmpty(where.getTablealias())) {
						whereColumnBuffer.append(where.getTablealias() + ".");
					}
					if (StringUtil.isNotEmpty(where.getName())) {
						whereColumnBuffer.append(where.getName());
					}
				}

				whereColumnBuffer.append(" ");
				whereColumnBuffer.append(where.getComparisonoperator());
				whereColumnBuffer.append(" ");
				if (StringUtil.isEmpty(where.getIfrule())) {
					whereBuffer.append(whereColumnBuffer);
				} else {
					JSONObject $ifrule = new JSONObject();
					$ifrule.put("condition", where.getIfrule());
					$ifrule.put("sql", whereColumnBuffer);
					$list.add($ifrule);
				}
			}
		}
	}

	public void buildCustomSqlData(JSONArray $list, CustomSql customSql) {
		if (customSql == null) {
			return;
		}
		JSONObject $one = new JSONObject();
		$one.put("sql", customSql.getSql());
		$list.add($one);

	}

	@Override
	public String getTemplate() throws Exception {

		DaoProcess daoProcess = dao.getProcess();

		if (DaoProcessType.SQL.getValue().equals(daoProcess.getType())) {
			return "template/dao/sql";
		} else if (DaoProcessType.HTTP.getValue().equals(daoProcess.getType())) {
			return "template/dao/http";
		} else if (DaoProcessType.CACHE.getValue().equals(daoProcess.getType())) {
			return "template/dao/cache";
		}
		return "template/dao/default";
	}

}
