package com.teamide.ide.generater.code.sql.mapper;

import java.util.List;

import com.teamide.app.enums.ComparisonOperator;
import com.teamide.app.process.dao.sql.Abstract;
import com.teamide.app.process.dao.sql.AppendCustomSql;
import com.teamide.app.process.dao.sql.PieceWhere;
import com.teamide.app.process.dao.sql.Where;
import com.teamide.ide.generater.code.sql.SqlGenerater;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public abstract class SqlMapperGenerater extends SqlGenerater {

	public SqlMapperGenerater(String factory_classname, Abstract base) {
		super(factory_classname, base);
	}

	public void appendWhere(int tab, List<Where> wheres) {

		if (wheres == null || wheres.size() == 0) {
			return;
		}
		if (wheres.get(0) instanceof PieceWhere) {
			content.append(getTab(tab)).append(" 1=1 ").append("\n");
		} else {
			content.append(getTab(tab)).append(" WHERE 1=1 ").append("\n");
		}
		for (Where where : wheres) {

			int t = tab;
			if (StringUtil.isNotTrimEmpty(where.getIfrule())) {
				content.append(getTab(tab));
				content.append("<if test=\"" + where.getIfrule() + "\" >").append("\n");
				t++;
			} else {
			}
			appendWhereSql(t, where);

			if (StringUtil.isNotTrimEmpty(where.getIfrule())) {
				content.append(getTab(tab)).append("</if>").append("\n");
			}

		}

	}

	public void appendWhereSql(int tab, Where where) {
		if (ObjectUtil.isTrue(where.getPiece())) {
			content.append(getTab(tab)).append(where.splice()).append("\n");
			appendPieceWhere(tab, where.getWheres());
			content.append(getTab(tab)).append(")").append("\n");
			return;
		}

		if (ObjectUtil.isTrue(where.getCustom())) {
			String customsql = where.getCustomsql();
			content.append(getTab(tab)).append(where.splice() + " (" + customsql + ") ").append("\n");
			return;
		}

		String placeKey = base.getPlaceKey(where.getName(), keyCache);
		placeKey = StringUtil.trim(placeKey);

		content.append(getTab(tab));
		content.append("<if test=\"" + placeKey + " != null && " + placeKey + " != ''\" >").append("\n");

		String whereName = "";
		String name = StringUtil.trim(where.getName());
		if (!StringUtil.isTrimEmpty(name)) {
			String tablealias = StringUtil.trim(where.getTablealias());
			if (!StringUtil.isEmpty(tablealias)) {
				whereName += tablealias + ".";
			}
			whereName += name;
		}

		String comparisonoperator = StringUtil.trim(where.getComparisonoperator());
		if (StringUtil.isEmpty(comparisonoperator)) {
			comparisonoperator = "=";
		}
		ComparisonOperator operator = ComparisonOperator.get(comparisonoperator);

		StringBuffer sql = new StringBuffer();
		sql.append(" ").append(where.splice()).append(" ");

		sql.append(whereName).append(" ");
		switch (operator) {
		case LIKE:
			sql.append(" LIKE ");
			break;
		case LIKE_AFTER:
			sql.append(" LIKE ");
			break;
		case LIKE_BEFORE:
			sql.append(" LIKE ");
			break;
		case IN:
			sql.append(" IN ");
			break;
		case NOT_IN:
			sql.append(" NOT IN ");
			break;
		default:
			sql.append(operator.getValue());
			break;
		}
		sql.append(" ").append("#{" + placeKey + "}");

		content.append(getTab(tab + 1));
		content.append(sql).append("\n");
		switch (operator) {
		default:
			break;
		}

		content.append(getTab(tab)).append("</if>").append("\n");

	}

	public void appendAppends(int tab, List<AppendCustomSql> appends) {

		if (appends == null || appends.size() == 0) {
			return;
		}
		boolean isFirst = true;
		for (AppendCustomSql append : appends) {

			if (StringUtil.isNotTrimEmpty(append.getIfrule())) {
				content.append(getTab(tab));
				content.append("<if test=\"" + append.getIfrule() + "\" >").append("\n");
				content.append(getTab(tab + 1));
			} else {
				content.append(getTab(tab));
			}

			StringBuffer sql = new StringBuffer();
			if (isFirst) {
				sql.append(" ");
			}
			sql.append(getAppendSql(append));
			sql.append(" ");

			content.append(sql).append("\n");

			if (StringUtil.isNotTrimEmpty(append.getIfrule())) {
				content.append(getTab(tab));
				content.append("</if>").append("\n");
			}

			isFirst = false;

		}

	}
}
