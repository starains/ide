package com.teamide.ide.generater.code.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamide.app.enums.ComparisonOperator;
import com.teamide.app.process.dao.sql.Abstract;
import com.teamide.app.process.dao.sql.AppendCustomSql;
import com.teamide.app.process.dao.sql.PieceWhere;
import com.teamide.app.process.dao.sql.Where;
import com.teamide.ide.generater.code.CodeGenerater;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public abstract class SqlGenerater extends CodeGenerater {

	protected final Abstract base;

	protected final Map<String, Integer> keyCache = new HashMap<String, Integer>();

	public abstract StringBuffer generate(int tab);

	public SqlGenerater(String factory_classname, Abstract base) {
		super(factory_classname);
		this.base = base;
	}

	public void appendPieceWhere(int tab, List<PieceWhere> pieceWheres) {
		if (pieceWheres == null || pieceWheres.size() == 0) {
			return;
		}

		List<Where> wheres = new ArrayList<Where>();
		for (PieceWhere pieceWhere : pieceWheres) {
			wheres.add(pieceWhere);
		}

		appendWhere(tab, wheres);
	}

	public void appendWhere(int tab, List<Where> wheres) {

		if (wheres == null || wheres.size() == 0) {
			return;
		}
		if (wheres.get(0) instanceof PieceWhere) {
			content.append(getTab(tab)).append("whereSql.append(\" 1=1 \");").append("\n");
		} else {
			content.append(getTab(tab)).append("whereSql.append(\" WHERE 1=1 \");").append("\n");
		}
		for (Where where : wheres) {

			int t = tab;
			if (StringUtil.isNotTrimEmpty(where.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(ObjectUtil.isTrue(" + factory_classname + ".getValueByJexlScript(\""
						+ where.getIfrule() + "\", data))) {").append("\n");
				t++;
			} else {
			}
			appendWhereSql(t, where);

			if (StringUtil.isNotTrimEmpty(where.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");
			}

		}

	}

	public void appendWhereSql(int tab, Where where) {
		if (ObjectUtil.isTrue(where.getPiece())) {
			content.append(getTab(tab)).append("whereSql.append(\" " + where.splice() + " (\");").append("\n");
			appendPieceWhere(tab, where.getWheres());
			content.append(getTab(tab)).append("whereSql.append(\")\");").append("\n");
			return;
		}

		if (ObjectUtil.isTrue(where.getCustom())) {
			String customsql = where.getCustomsql();
			content.append(getTab(tab)).append("whereSql.append(\" " + where.splice() + " (" + customsql + ")\");")
					.append("\n");
			return;
		}

		if (StringUtil.isNotTrimEmpty(where.getValuer())) {

			content.append(getTab(tab));
			content.append("value = new " + where.getValuer() + "().getValue();").append("\n");
		} else {
			if (StringUtil.isNotTrimEmpty(where.getValue())) {
				content.append(getTab(tab));
				content.append(
						"value = " + factory_classname + ".getValueByJexlScript(\"" + where.getValue() + "\", data);")
						.append("\n");
			} else {
				content.append(getTab(tab));
				content.append("value = data.get(\"" + where.getName() + "\");").append("\n");

				if (StringUtil.isNotTrimEmpty(where.getDefaultvalue())) {
					content.append(getTab(tab));
					content.append("if(value == null || StringUtil.isEmptyIfStr(value)) {").append("\n");

					content.append(getTab(tab + 1));
					content.append("value = " + factory_classname + ".getValueByJexlScript(\"" + where.getDefaultvalue()
							+ "\", data);").append("\n");

					content.append(getTab(tab)).append("}").append("\n");
				}

			}
		}

		content.append(getTab(tab));
		content.append("if(value != null && !StringUtil.isEmptyIfStr(value)) {").append("\n");

		String whereName = "";
		String name = StringUtil.trim(where.getName());
		if (!StringUtil.isTrimEmpty(name)) {
			String tablealias = StringUtil.trim(where.getTablealias());
			if (!StringUtil.isEmpty(tablealias)) {
				whereName += tablealias + ".";
			}
			whereName += name;
		}

		String placeKey = base.getPlaceKey(where.getName(), keyCache);
		placeKey = StringUtil.trim(placeKey);

		String comparisonoperator = StringUtil.trim(where.getComparisonoperator());
		if (StringUtil.isEmpty(comparisonoperator)) {
			comparisonoperator = "=";
		}
		ComparisonOperator operator = ComparisonOperator.get(comparisonoperator);

		StringBuffer sql = new StringBuffer();
		sql.append(" ").append(where.splice()).append(" ");
		if (operator.equals(ComparisonOperator.IN_LIKE) || operator.equals(ComparisonOperator.IN)
				|| operator.equals(ComparisonOperator.NOT_IN)) {
			switch (operator) {
			case IN:
				sql.append(whereName).append(" ");
				sql.append(" IN ");

				content.append(getTab(tab + 1));
				content.append("whereSql.append(\"" + sql + "\");").append("\n");

				content.append(getTab(tab + 1));
				content.append("value = \"('\" + String.join(\"','\", String.valueOf(value).split(\",\")) + \"')\";")
						.append("\n");
				content.append(getTab(tab + 1));
				content.append("whereSql.append(value);").append("\n");

				break;
			case IN_LIKE:
				sql.append(" LIKE ");
				break;
			case NOT_IN:
				sql.append(whereName).append(" ");
				sql.append(" NOT IN ");

				content.append(getTab(tab + 1));
				content.append("whereSql.append(\"" + sql + "\");").append("\n");

				content.append(getTab(tab + 1));
				content.append("value = \"('\" + String.join(\"','\", String.valueOf(value).split(\",\")) + \"')\";")
						.append("\n");

				content.append(getTab(tab + 1));
				content.append("whereSql.append(value);").append("\n");
				break;
			default:
				break;
			}

		} else {
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
			default:
				sql.append(operator.getValue());
				break;
			}
			sql.append(" ").append(":" + placeKey);

			content.append(getTab(tab + 1));
			content.append("whereSql.append(\"" + sql + "\");").append("\n");
			switch (operator) {
			case LIKE:
				content.append(getTab(tab + 1));
				content.append("value = \"%\" + String.valueOf(value) + \"%\";").append("\n");
				break;
			case LIKE_BEFORE:
				content.append(getTab(tab + 1));
				content.append("value = \"%\" + String.valueOf(value);").append("\n");
				break;
			case LIKE_AFTER:
				content.append(getTab(tab + 1));
				content.append("value = String.valueOf(value) + \"%\";").append("\n");
				break;
			default:
				break;
			}
		}

		content.append(getTab(tab + 1));
		content.append("param.put(\"" + placeKey + "\", value);").append("\n");

		if (ObjectUtil.isTrue(where.getRequired())) {
			content.append(getTab(tab)).append("} else {").append("\n");

			content.append(getTab(tab + 1))
					.append("throw new FieldValidateException(\"" + where.getName() + " value is required.\");")
					.append("\n");

		}

		content.append(getTab(tab)).append("}").append("\n");

	}

	public void appendAppends(int tab, List<AppendCustomSql> appends) {

		if (appends == null || appends.size() == 0) {
			return;
		}
		boolean isFirst = true;
		for (AppendCustomSql append : appends) {

			if (StringUtil.isNotTrimEmpty(append.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(ObjectUtil.isTrue(" + factory_classname + ".getValueByJexlScript(\""
						+ append.getIfrule() + "\", data))) {").append("\n");
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

			content.append("appendSql.append(\"" + sql + "\");").append("\n");

			if (StringUtil.isNotTrimEmpty(append.getIfrule())) {
				content.append(getTab(tab));
				content.append("}").append("\n");
			}

			isFirst = false;

		}

	}

	public StringBuffer getAppendSql(AppendCustomSql append) {
		StringBuffer sql = new StringBuffer();
		sql.append(" ");
		if (StringUtil.isNotEmpty(append.getSql())) {
			sql.append(append.getSql());
		}
		return sql;

	}

	protected void ignoreWhereTablealias(List<Where> wheres) {
		if (wheres != null) {
			for (Where where : wheres) {
				ignoreWhereTablealias(where);
			}
		}
	}

	protected void ignoreWhereTablealias(Where where) {
		if (where != null) {
			where.setTablealias(null);
			if (where.getWheres() != null) {
				for (PieceWhere pieceWhere : where.getWheres()) {
					ignoreWhereTablealias(pieceWhere);
				}
			}
		}
	}
}