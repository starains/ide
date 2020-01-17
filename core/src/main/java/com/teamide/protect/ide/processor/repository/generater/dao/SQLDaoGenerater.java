package com.teamide.protect.ide.processor.repository.generater.dao;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.process.dao.DaoSqlProcess;
import com.teamide.app.process.dao.sql.CustomSql;
import com.teamide.app.process.dao.sql.From;
import com.teamide.app.process.dao.sql.Select;
import com.teamide.app.process.dao.sql.SelectColumn;
import com.teamide.app.process.dao.sql.Where;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.project.AppBean;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public abstract class SQLDaoGenerater extends BaseDaoGenerater {

	public SQLDaoGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
	}

	public void buildSQLData() {
		DaoSqlProcess sqlProcess = (DaoSqlProcess) dao.getProcess();
		JSONArray $list = new JSONArray();
		data.put("$list", $list);
		data.put("$sqlType", sqlProcess.getSqlType());
		boolean isPage = false;
		if (sqlProcess.getSqlType().startsWith("CUSTOM_SQL")) {
			buildCustomSqlData($list, sqlProcess.getCustomSql());
		} else if (sqlProcess.getSqlType().startsWith("SELECT")) {
			if (sqlProcess.getSqlType().indexOf("PAGE") >= 0) {
				isPage = true;
			}
			buildSelectSqlData($list, sqlProcess.getSelect(), isPage);
		}

	}

	public void buildSelectSqlData(JSONArray $list, Select select, boolean isPage) {
		if (select == null) {
			return;
		}
		JSONObject $select = new JSONObject();
		StringBuffer selectBuffer = new StringBuffer();
		selectBuffer.append("SELECT ");
		$select.put("sql", selectBuffer);
		$list.add($select);

		JSONObject $selectCount = new JSONObject();
		StringBuffer selectCountBuffer = new StringBuffer();
		selectCountBuffer.append("SELECT COUNT(1) ");
		$selectCount.put("countSql", selectCountBuffer);
		if (isPage) {
			$list.add($selectCount);
		}
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
			selectCountBuffer.append(" FROM");
			$from.put("sql", fromBuffer);
			$list.add($from);
			for (From from : froms) {
				if (StringUtil.isNotEmpty(from.getTable())) {
					fromBuffer.append(" " + from.getTable());
					selectCountBuffer.append(" " + from.getTable());
				}
				if (StringUtil.isNotEmpty(from.getAlias())) {
					fromBuffer.append(" AS " + from.getAlias());
					selectCountBuffer.append(" AS " + from.getAlias());
				}
			}

		}
		List<Where> wheres = select.getWheres();
		buildWhereSqlData($list, wheres);
	}

	public void buildWhereSqlData(JSONArray $list, List<Where> wheres) {
		if (wheres == null || wheres.size() == 0) {
			return;
		}
		for (Where where : wheres) {

			JSONObject $where = new JSONObject();
			StringBuffer whereBuffer = new StringBuffer();
			$where.put("whereSql", whereBuffer);
			$list.add($where);

			JSONObject $param = null;
			StringBuffer whereColumnBuffer = new StringBuffer();
			whereColumnBuffer.append(" ");
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
				$param = new JSONObject();
				$param.put("key", where.getName());
				$param.put("value", where.getValue());
				$param.put("defaultvalue", where.getDefaultvalue());
				whereColumnBuffer.append(" ");
				whereColumnBuffer.append(where.getComparisonoperator());
				whereColumnBuffer.append(" ");
				whereColumnBuffer.append(":" + where.getName());
			}

			if (StringUtil.isEmpty(where.getIfrule())) {
				whereBuffer.append(whereColumnBuffer);
				if ($param != null) {
					JSONObject $one = new JSONObject();
					$one.put("$param", $param);
					$list.add($one);
				}
			} else {
				JSONObject $ifrule = new JSONObject();
				$ifrule.put("condition", where.getIfrule());
				$ifrule.put("whereSql", whereColumnBuffer);
				$ifrule.put("$param", $param);
				$list.add($ifrule);
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
}
