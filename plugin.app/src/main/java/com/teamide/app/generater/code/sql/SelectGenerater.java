package com.teamide.app.generater.code.sql;

import java.util.ArrayList;
import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.process.dao.sql.From;
import com.teamide.app.process.dao.sql.Group;
import com.teamide.app.process.dao.sql.Having;
import com.teamide.app.process.dao.sql.LeftJoin;
import com.teamide.app.process.dao.sql.Order;
import com.teamide.app.process.dao.sql.Select;
import com.teamide.app.process.dao.sql.SelectColumn;
import com.teamide.app.process.dao.sql.Union;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public class SelectGenerater extends SqlGenerater {

	protected final Select select;

	public SelectGenerater(AppContext context, Select select) {
		super(context, select);
		this.select = select;
	}

	public void doGenerate(int tab) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		if (select.isDistinct()) {
			sql.append("DISTINCT ");
		}

		content.append(getTab(tab)).append("// 组合查询字段语句").append("\n");
		content.append(getTab(tab)).append("sql.append(\"" + sql + "\");").append("\n");

		content_mapper.append(getTab(tab)).append(sql).append("\n");
		content_count_mapper.append(getTab(tab)).append(sql).append("\n");

		List<StringBuffer> selectColumnSqls = getSelectColumns(tab);
		for (StringBuffer selectColumnSql : selectColumnSqls) {
			content.append(getTab(tab)).append("sql.append(\"" + selectColumnSql + "\");").append("\n");

			content_mapper.append(getTab(tab)).append(selectColumnSql).append("\n");
		}

		appendConditionColumns(tab);

		content_count_mapper.append(getTab(tab)).append(" COUNT(1) ").append("\n");

		content.append("\n");
		content.append(getTab(tab)).append("// 组合查询SQL From语句").append("\n");
		appendFrom(tab);
		appendLeftJoin(tab);

		content.append("\n");
		content.append(getTab(tab)).append("// 拼接SQL").append("\n");
		content.append(getTab(tab)).append("sql.append(fromSql);").append("\n");
		content.append("\n");

		content.append(getTab(tab)).append("// 组合条件语句").append("\n");
		appendWhere(tab, select.getWheres());

		content.append("\n");
		content.append(getTab(tab)).append("// 拼接SQL").append("\n");
		content.append(getTab(tab)).append("sql.append(whereSql);").append("\n");
		content.append("\n");

		appendGroup(tab);

		content.append("\n");
		content.append(getTab(tab)).append("// 拼接SQL").append("\n");
		content.append(getTab(tab)).append("sql.append(groupSql);").append("\n");
		content.append("\n");

		appendHaving(tab);
		appendOrder(tab);

		if (select.isForupdate()) {
			content.append(getTab(tab));
			content.append("sql.append(\" FOR UPDATE \");").append("\n");
			if (select.isNowait()) {
				content.append(getTab(tab));
				content.append("sql.append(\" NOWAIT \");").append("\n");
			}
		}
		appendUnion(tab);

		if (select.getAppends() != null && select.getAppends().size() > 0) {
			content.append("\n");
			content.append(getTab(tab)).append("// 追加SQL").append("\n");
			appendAppends(tab, select.getAppends());
		}

	}

	public List<StringBuffer> getSelectColumns(int tab) {

		List<StringBuffer> sqls = new ArrayList<StringBuffer>();

		List<SelectColumn> columns = select.getColumns();
		if (columns == null || columns.size() == 0) {
		} else {
			boolean isFirst = true;
			StringBuffer lastSql = new StringBuffer();
			for (SelectColumn column : columns) {
				if (StringUtil.isNotTrimEmpty(column.getIfrule())) {
					continue;
				}
				if (ObjectUtil.isTrue(column.getCustom())) {
					if (lastSql.length() > 0) {
						sqls.add(lastSql);
						lastSql = new StringBuffer();
					}
				}

				if (!isFirst) {
					lastSql.append(",");
				}
				lastSql.append(getColumnSql(column, tab));
				if (lastSql.length() > 80) {
					sqls.add(lastSql);
					lastSql = new StringBuffer();
				}
				isFirst = false;
			}
			if (lastSql.length() > 0) {
				sqls.add(lastSql);
				lastSql = new StringBuffer();
			}
		}
		if (sqls.size() == 0) {
			sqls.add(new StringBuffer("*"));
		}

		return sqls;
	}

	public StringBuffer getColumnSql(SelectColumn column, int tab) {
		StringBuffer sql = new StringBuffer();
		if (ObjectUtil.isTrue(column.getCustom())) {
			String customsql = formatCustomToContentSql(column.getCustomsql(), tab);
			sql.append(customsql);
		} else {
			String name = StringUtil.trim(column.getName());
			if (!StringUtil.isEmpty(name)) {
				String tablealias = StringUtil.trim(column.getTablealias());
				if (!StringUtil.isEmpty(tablealias)) {
					sql.append(tablealias).append(".");
				}
				String nameCustomSql = formatCustomToContentSql(name, tab);
				sql.append(nameCustomSql);
			}
		}
		String alias = StringUtil.trim(column.getAlias());
		if (!StringUtil.isEmpty(alias)) {
			sql.append(" AS ");
			sql.append(alias);
		}
		return sql;
	}

	public void appendConditionColumns(int tab) {

		List<SelectColumn> columns = select.getColumns();
		if (columns == null || columns.size() == 0) {
			return;
		}
		for (SelectColumn column : columns) {
			if (StringUtil.isTrimEmpty(column.getIfrule())) {
				continue;
			}

			content.append(getTab(tab));
			content.append("if(" + getFormatIfruleForContent(column.getIfrule()) + ") {").append("\n");

			content_mapper.append(getTab(tab));
			content_mapper
					.append("<if test=\"" + getFormatIfruleForMapper(column.getIfrule(), variablesForMapper) + "\" >")
					.append("\n");

			StringBuffer sql = new StringBuffer();
			sql.append(",");
			sql.append(getColumnSql(column, tab));

			content.append(getTab(tab + 1)).append("sql.append(\"" + sql + "\");").append("\n");

			content_mapper.append(getTab(tab + 1)).append(sql).append("\n");

			content.append(getTab(tab));
			content.append("}").append("\n");

			content_mapper.append(getTab(tab)).append("</if>").append("\n");
		}

	}

	public void appendFrom(int tab) {

		List<From> froms = select.getFroms();
		if (froms == null || froms.size() == 0) {
			return;
		}
		boolean isFirst = true;
		for (From from : froms) {
			int tab_ = tab;
			if (StringUtil.isNotTrimEmpty(from.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(" + getFormatIfruleForContent(from.getIfrule()) + ") {").append("\n");

				content_mapper.append(getTab(tab));
				content_mapper
						.append("<if test=\"" + getFormatIfruleForMapper(from.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");

				content_count_mapper.append(getTab(tab));
				content_count_mapper
						.append("<if test=\"" + getFormatIfruleForMapper(from.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");

				tab_ = tab + 1;
			} else {
			}

			content.append(getTab(tab_));

			content_mapper.append(getTab(tab_));
			content_count_mapper.append(getTab(tab_));

			StringBuffer sql = new StringBuffer();
			if (isFirst) {
				sql.append(" FROM ");
			}
			if (shouldTableVariableDefinition(from.getTable())) {
				String code = getTableVariableDefinitionCode(from.getTable());

				content.append(code).append("\n");

				content.append(getTab(tab_));

				sql.append(getFromSql(from));
				sql.append(" ");
			} else {
				sql.append(getFromSql(from));
				sql.append(" ");
			}

			content.append("fromSql.append(\"" + sql + "\");").append("\n");

			content_mapper.append(sql).append("\n");
			content_count_mapper.append(sql).append("\n");

			if (StringUtil.isNotTrimEmpty(from.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				content_mapper.append(getTab(tab)).append("</if>").append("\n");
				content_count_mapper.append(getTab(tab)).append("</if>").append("\n");
			}

			isFirst = false;

		}

	}

	public StringBuffer getFromSql(From from) {
		StringBuffer sql = new StringBuffer();
		String table = StringUtil.trim(from.getTable());
		if (shouldTableVariableDefinition(table)) {
			sql.append("\" + TABLE_" + table + " + \"");
		} else {
			if (!StringUtil.isEmpty(table)) {
				sql.append(table);
			}

		}
		String alias = StringUtil.trim(from.getAlias());
		if (!StringUtil.isEmpty(alias)) {
			sql.append(" AS ");
			sql.append(alias);
		}
		return sql;

	}

	public void appendLeftJoin(int tab) {

		List<LeftJoin> leftJoins = select.getLeftjoins();
		if (leftJoins == null || leftJoins.size() == 0) {
			return;
		}
		boolean isFirst = true;
		for (LeftJoin leftJoin : leftJoins) {

			int tab_ = tab;
			if (StringUtil.isNotTrimEmpty(leftJoin.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(" + getFormatIfruleForContent(leftJoin.getIfrule()) + ") {").append("\n");

				content_mapper.append(getTab(tab));
				content_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(leftJoin.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");

				content_count_mapper.append(getTab(tab));
				content_count_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(leftJoin.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");
				tab_ = tab + 1;
			} else {
			}

			content.append(getTab(tab_));

			content_mapper.append(getTab(tab_));
			content_count_mapper.append(getTab(tab_));

			StringBuffer sql = new StringBuffer();
			if (isFirst) {
				sql.append(" ");
			}
			if (shouldTableVariableDefinition(leftJoin.getTable())) {
				String code = getTableVariableDefinitionCode(leftJoin.getTable());

				content.append(code).append("\n");

				content.append(getTab(tab_));

				sql.append(getLeftJoinSql(leftJoin));
			} else {
				sql.append(getLeftJoinSql(leftJoin));
			}
			sql.append(" ");

			content.append("fromSql.append(\"" + sql + "\");").append("\n");

			content_mapper.append(sql).append("\n");
			content_count_mapper.append(sql).append("\n");

			if (StringUtil.isNotTrimEmpty(leftJoin.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				content_mapper.append(getTab(tab)).append("</if>").append("\n");
				content_count_mapper.append(getTab(tab)).append("</if>").append("\n");
			}

			isFirst = false;

		}

	}

	public StringBuffer getLeftJoinSql(LeftJoin leftJoin) {
		StringBuffer sql = new StringBuffer();
		sql.append(" LEFT JOIN ");
		String table = StringUtil.trim(leftJoin.getTable());
		if (shouldTableVariableDefinition(table)) {
			sql.append("\" + TABLE_" + table + " + \"");
		} else {
			if (!StringUtil.isEmpty(table)) {
				sql.append(table);
			}
		}
		String alias = StringUtil.trim(leftJoin.getAlias());
		if (!StringUtil.isEmpty(alias)) {
			sql.append(" AS ");
			sql.append(alias);
		}
		String on = StringUtil.trim(leftJoin.getOn());
		if (!StringUtil.isEmpty(on)) {
			sql.append(" ON ");
			sql.append(on);
		}
		return sql;

	}

	public void appendGroup(int tab) {

		List<Group> groups = select.getGroups();
		if (groups == null || groups.size() == 0) {
			return;
		}
		boolean isFirst = true;

		content.append(getTab(tab));
		content.append("groupSql.append(\" GROUP BY \");").append("\n");

		content_mapper.append(getTab(tab));
		content_mapper.append(" GROUP BY ").append("\n");

		content_count_mapper.append(getTab(tab));
		content_count_mapper.append(" GROUP BY ").append("\n");

		content.append(getTab(tab));
		content.append("boolean isGroupFirst = true;").append("\n");
		for (Group group : groups) {

			int tab_ = tab;
			if (StringUtil.isNotTrimEmpty(group.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(" + getFormatIfruleForContent(group.getIfrule()) + ") {").append("\n");

				content_mapper.append(getTab(tab));
				content_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(group.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");

				content_count_mapper.append(getTab(tab));
				content_count_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(group.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");

				tab_ = tab + 1;
			} else {

			}

			StringBuffer sql = new StringBuffer();
			if (isFirst) {
				sql.append(" ");
			}
			sql.append(getGroupSql(group));
			sql.append(" ");

			String contentSql = formatCustomToContentSql(sql.toString(), tab);
			String mapperSql = formatCustomToMapperSql(sql.toString());

			content.append(getTab(tab_));
			content.append("groupSql.append(isGroupFirst ? \"\" : \",\").append(\"" + contentSql + "\");").append("\n");
			content.append(getTab(tab_));
			content.append("isGroupFirst = false;").append("\n");

			content_mapper.append(getTab(tab_));
			content_mapper.append(contentSql).append("\n");

			content_count_mapper.append(getTab(tab_));
			content_count_mapper.append(mapperSql).append("\n");

			if (StringUtil.isNotTrimEmpty(group.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				content_mapper.append(getTab(tab)).append("</if>").append("\n");
				content_count_mapper.append(getTab(tab)).append("</if>").append("\n");
			}

			isFirst = false;

		}

	}

	public StringBuffer getGroupSql(Group group) {
		StringBuffer sql = new StringBuffer();
		if (ObjectUtil.isTrue(group.getCustom())) {
			sql.append(group.getCustomsql());
		} else {
			String name = StringUtil.trim(group.getName());
			if (!StringUtil.isEmpty(name)) {
				String tablealias = StringUtil.trim(group.getTablealias());
				if (!StringUtil.isEmpty(tablealias)) {
					sql.append(tablealias).append(".");
				}
				sql.append(name);
			}
		}
		return sql;

	}

	public void appendHaving(int tab) {

		List<Having> havings = select.getHavings();
		if (havings == null || havings.size() == 0) {
			return;
		}

		content.append(getTab(tab));
		content.append("sql.append(\" HAVING \");").append("\n");

		content_mapper.append(getTab(tab));
		content_mapper.append(" HAVING ").append("\n");
		boolean isFirst = true;
		for (Having having : havings) {

			if (StringUtil.isNotTrimEmpty(having.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(" + getFormatIfruleForContent(having.getIfrule()) + ") {").append("\n");
				content.append(getTab(tab + 1));

				content_mapper.append(getTab(tab));
				content_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(having.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");
				content_mapper.append(getTab(tab + 1));

			} else {
				content.append(getTab(tab));

				content_mapper.append(getTab(tab));
			}

			StringBuffer sql = new StringBuffer();
			if (isFirst) {
				sql.append(" ");
			}
			sql.append(getHavingSql(having));
			sql.append(" ");

			String contentSql = formatCustomToContentSql(sql.toString(), tab);
			String mapperSql = formatCustomToMapperSql(sql.toString());

			content.append("sql.append(\"" + contentSql + "\");").append("\n");

			content_mapper.append(mapperSql).append("\n");

			if (StringUtil.isNotTrimEmpty(having.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				content_mapper.append(getTab(tab)).append("</if>").append("\n");
			}

			isFirst = false;

		}

	}

	public StringBuffer getHavingSql(Having having) {
		StringBuffer sql = new StringBuffer();
		if (ObjectUtil.isTrue(having.getCustom())) {
			sql.append(having.getCustomsql());
		} else {
			String name = StringUtil.trim(having.getName());
			if (!StringUtil.isEmpty(name)) {
				String tablealias = StringUtil.trim(having.getTablealias());
				if (!StringUtil.isEmpty(tablealias)) {
					sql.append(tablealias).append(".");
				}
				sql.append(name);
			}
		}
		return sql;

	}

	public void appendOrder(int tab) {

		List<Order> orders = select.getOrders();
		if (orders == null || orders.size() == 0) {
			return;
		}
		content.append(getTab(tab));
		content.append("sql.append(\" ORDER BY \");").append("\n");

		content_mapper.append(getTab(tab));
		content_mapper.append(" ORDER BY ").append("\n");

		content.append(getTab(tab));
		content.append("boolean isOrderFirst = true;").append("\n");

		boolean isFirst = true;
		for (Order order : orders) {
			int tab_ = tab;
			if (StringUtil.isNotTrimEmpty(order.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(" + getFormatIfruleForContent(order.getIfrule()) + ") {").append("\n");

				content_mapper.append(getTab(tab));
				content_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(order.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");
				tab_ = tab + 1;

			} else {

			}

			StringBuffer sql = new StringBuffer();
			if (isFirst) {
				sql.append(" ");
			}
			sql.append(getOrderSql(order));
			sql.append(" ");

			String contentSql = formatCustomToContentSql(sql.toString(), tab);
			String mapperSql = formatCustomToMapperSql(sql.toString());

			content.append(getTab(tab_));
			content.append("sql.append(isOrderFirst ? \"\" : \",\").append(\"" + contentSql + "\");").append("\n");
			content.append(getTab(tab_));
			content.append("isOrderFirst = false;").append("\n");

			content_mapper.append(getTab(tab_));
			content_mapper.append(mapperSql).append("\n");

			if (StringUtil.isNotTrimEmpty(order.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				content_mapper.append(getTab(tab)).append("</if>").append("\n");
			}

			isFirst = false;
		}
	}

	public StringBuffer getOrderSql(Order order) {
		StringBuffer sql = new StringBuffer();
		if (ObjectUtil.isTrue(order.getCustom())) {
			sql.append(order.getCustomsql());
		} else {
			String name = StringUtil.trim(order.getName());
			if (!StringUtil.isEmpty(name)) {
				String tablealias = StringUtil.trim(order.getTablealias());
				if (!StringUtil.isEmpty(tablealias)) {
					sql.append(tablealias).append(".");
				}
				sql.append(name);
			}

			String ord = StringUtil.trim(order.getOrder());
			if (!StringUtil.isEmpty(ord)) {
				sql.append(" ");
				sql.append(ord);
			}
		}
		return sql;

	}

	public void appendUnion(int tab) {

		List<Union> unions = select.getUnions();
		if (unions == null || unions.size() == 0) {
			return;
		}

		content.append(getTab(tab));
		content.append("groupSql.append(\" UNION \");").append("\n");

		content_mapper.append(getTab(tab));
		content_mapper.append(" UNION ").append("\n");
		boolean isFirst = true;
		for (Union union : unions) {

			if (StringUtil.isNotTrimEmpty(union.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(" + getFormatIfruleForContent(union.getIfrule()) + ") {").append("\n");
				content.append(getTab(tab + 1));

				content_mapper.append(getTab(tab));
				content_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(union.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");
				content_mapper.append(getTab(tab + 1));

			} else {
				content.append(getTab(tab));

				content_mapper.append(getTab(tab));
			}

			StringBuffer sql = new StringBuffer();
			if (isFirst) {
				sql.append(" ");
			}
			sql.append(getUnionSql(union));
			sql.append(" ");

			content.append("sql.append(\"" + sql + "\");").append("\n");

			content_mapper.append(sql).append("\n");

			if (StringUtil.isNotTrimEmpty(union.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				content_mapper.append(getTab(tab)).append("</if>").append("\n");
			}

			isFirst = false;

		}

	}

	public StringBuffer getUnionSql(Union union) {
		StringBuffer sql = new StringBuffer();
		return sql;

	}
}
