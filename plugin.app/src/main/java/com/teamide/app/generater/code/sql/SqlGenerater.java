package com.teamide.app.generater.code.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DatabaseBean;
import com.teamide.app.bean.TableBean;
import com.teamide.app.enums.ComparisonOperator;
import com.teamide.app.generater.code.CodeGenerater;
import com.teamide.app.process.dao.sql.Abstract;
import com.teamide.app.process.dao.sql.AppendCustomSql;
import com.teamide.app.process.dao.sql.PieceWhere;
import com.teamide.app.process.dao.sql.Where;
import com.teamide.app.variable.Variable;
import com.teamide.app.variable.VariableBean;
import com.teamide.app.variable.VariableValidate;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public abstract class SqlGenerater extends CodeGenerater {

	protected final StringBuffer content_mapper = new StringBuffer();

	protected final StringBuffer content_count_mapper = new StringBuffer();

	protected final Abstract base;

	protected final Map<String, Integer> keyCache = new HashMap<String, Integer>();

	protected final Map<String, String> ruleCacheForMapper = new HashMap<String, String>();

	protected final Map<String, String> ruleCacheForContent = new HashMap<String, String>();

	protected final Map<String, String> tableVariableCache = new HashMap<String, String>();

	protected final List<VariableValidate> validatesForMapper = new ArrayList<VariableValidate>();

	protected final List<Variable> variablesForMapper = new ArrayList<Variable>();

	protected final List<Variable> variablesForParam = new ArrayList<Variable>();

	protected final AppContext context;

	public SqlGenerater(AppContext context, Abstract base) {
		super("data", "data");
		this.context = context;
		this.base = base;
	}

	public void generate(int tab) {
		content.setLength(0);
		content_mapper.setLength(0);
		content_count_mapper.setLength(0);
		keyCache.clear();
		tableVariableCache.clear();
		validatesForMapper.clear();
		variablesForMapper.clear();
		variablesForParam.clear();
		doGenerate(tab);
	}

	protected abstract void doGenerate(int tab);

	public boolean shouldTableVariableDefinition(String tablename) {
		if (StringUtil.isEmpty(tablename)) {
			return false;
		}
		DatabaseBean database = getDatabaseByTableName(tablename);
		if (database != null) {
			if (ObjectUtil.isTrue(database.getMustbringname())) {
				return true;
			}
		}
		TableBean table = getTable(tablename);
		if (table != null) {
			if (ObjectUtil.isTrue(table.getDynamic())) {
				return true;
			}
		}
		return false;
	}

	public String getTableVariableDefinitionCode(String tablename) {
		if (StringUtil.isEmpty(tablename)) {
			return null;
		}
		if (!shouldTableVariableDefinition(tablename)) {
			return null;
		}
		tablename = tablename.trim();
		StringBuffer code = new StringBuffer();
		if (tableVariableCache.get(tablename) == null) {
			code.append("String TABLE_" + tablename + " = ");
			tableVariableCache.put(tablename, tablename);
		} else {
			code.append("TABLE_" + tablename + " = ");
		}
		code.append("factory.wrapTableName(\"" + tablename + "\", data);");
		return code.toString();
	}

	public DatabaseBean getDatabaseByTableName(String tablename) {
		TableBean table = getTable(tablename);
		String databasename = null;
		if (table != null) {
			databasename = table.getDatabasename();
		}
		return getDatabase(databasename);

	}

	public DatabaseBean getDatabase(String databasename) {
		if (StringUtil.isEmpty(databasename)) {
			return context.getJdbc();
		}
		return context.get(DatabaseBean.class, databasename);

	}

	public TableBean getTable(String tablename) {
		return context.get(TableBean.class, tablename);

	}

	public StringBuffer getContentMapper() {
		return content_mapper;
	}

	public StringBuffer getContentCountMapper() {
		return content_count_mapper;
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

			content_mapper.append(getTab(tab)).append(" 1=1 ").append("\n");
			content_count_mapper.append(getTab(tab)).append(" 1=1 ").append("\n");
		} else {
			content.append(getTab(tab)).append("whereSql.append(\" WHERE 1=1 \");").append("\n");

			content_mapper.append(getTab(tab)).append(" WHERE 1=1 ").append("\n");
			content_count_mapper.append(getTab(tab)).append(" WHERE 1=1 ").append("\n");
		}
		for (Where where : wheres) {

			int t = tab;
			if (StringUtil.isNotTrimEmpty(where.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(" + getFormatIfruleForContent(where.getIfrule()) + ") {").append("\n");

				content_mapper.append(getTab(tab));
				content_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(where.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");

				content_count_mapper.append(getTab(tab));
				content_count_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(where.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");

				t++;
			} else {
			}
			appendWhereSql(t, where);

			if (StringUtil.isNotTrimEmpty(where.getIfrule())) {
				content.append(getTab(tab)).append("}").append("\n");

				content_mapper.append(getTab(tab)).append("</if>").append("\n");
				content_count_mapper.append(getTab(tab)).append("</if>").append("\n");
			}

		}

	}

	public void appendWhereSql(int tab, Where where) {
		if (ObjectUtil.isTrue(where.getPiece())) {
			content.append(getTab(tab)).append("whereSql.append(\" " + where.splice() + " (\");").append("\n");

			content_mapper.append(getTab(tab)).append(" " + where.splice() + " (").append("\n");
			content_count_mapper.append(getTab(tab)).append(" " + where.splice() + " (").append("\n");

			appendPieceWhere(tab, where.getWheres());
			content.append(getTab(tab)).append("whereSql.append(\")\");").append("\n");

			content_mapper.append(getTab(tab)).append(")").append("\n");
			content_count_mapper.append(getTab(tab)).append(")").append("\n");
			return;
		}

		if (ObjectUtil.isTrue(where.getCustom())) {
			String customsql = where.getCustomsql();

			String contentSql = formatCustomToContentSql(customsql, tab);
			String mapperSql = formatCustomToMapperSql(customsql);

			content.append(getTab(tab));
			content.append("whereSql.append(\" " + where.splice() + " (" + contentSql + ")\");").append("\n");

			content_mapper.append(getTab(tab));
			content_mapper.append(" " + where.splice() + " (" + mapperSql + ")").append("\n");
			content_count_mapper.append(getTab(tab));
			content_count_mapper.append(" " + where.splice() + " (" + mapperSql + ")").append("\n");
			return;
		}

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

		if (StringUtil.isNotTrimEmpty(where.getValuer()) || StringUtil.isNotTrimEmpty(where.getValue())
				|| StringUtil.isNotTrimEmpty(where.getDefaultvalue())) {

			VariableBean variable = new VariableBean();
			variable.setValuer(where.getValuer());
			variable.setValue(where.getValue());
			variable.setDefaultvalue(where.getDefaultvalue());
			variable.setName(placeKey);
			variablesForMapper.add(variable);
		} else {
			if (!placeKey.equals(name)) {
				VariableBean variable = new VariableBean();
				variable.setValue(name);
				variable.setName(placeKey);
				variablesForMapper.add(variable);
			}
		}
		if (ObjectUtil.isTrue(where.getRequired())) {
			VariableValidate validate = new VariableValidate();
			validate.setRequired(true);
			validate.setValue(placeKey);
			validatesForMapper.add(validate);
		}

		if (StringUtil.isNotTrimEmpty(where.getValuer())) {

			content.append(getTab(tab));
			content.append("value = new " + where.getValuer() + "().getValue();").append("\n");

		} else {
			if (StringUtil.isNotTrimEmpty(where.getValue())) {
				content.append(getTab(tab));
				content.append("value = factory.getValueByJexlScript(\"" + where.getValue() + "\", data);")
						.append("\n");
			} else {
				content.append(getTab(tab));
				content.append("value = data.get(\"" + where.getName() + "\");").append("\n");

				if (StringUtil.isNotTrimEmpty(where.getDefaultvalue())) {
					content.append(getTab(tab));
					content.append("if(value == null || StringUtil.isEmptyIfStr(value)) {").append("\n");

					content.append(getTab(tab + 1));
					content.append("value = factory.getValueByJexlScript(\"" + where.getDefaultvalue() + "\", data);")
							.append("\n");

					content.append(getTab(tab)).append("}").append("\n");
				}

			}
		}

		content.append(getTab(tab));
		content.append("if(value != null && !StringUtil.isEmptyIfStr(value)) {").append("\n");

		if (!ObjectUtil.isTrue(where.getRequired())) {
			content_mapper.append(getTab(tab));
			content_mapper.append("<if test=\"" + placeKey + " != null and " + placeKey + " != ''\">").append("\n");
			content_count_mapper.append(getTab(tab));
			content_count_mapper.append("<if test=\"" + placeKey + " != null and " + placeKey + " != ''\">")
					.append("\n");
		}

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

			content_mapper.append(getTab(tab));
			content_mapper.append(sql + " #{" + placeKey + "}").append("\n");
			content_count_mapper.append(getTab(tab));
			content_count_mapper.append(sql + " #{" + placeKey + "}").append("\n");

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

			content_mapper.append(getTab(tab));
			content_mapper.append(sql + " #{" + placeKey + "}").append("\n");
			content_count_mapper.append(getTab(tab));
			content_count_mapper.append(sql + " #{" + placeKey + "}").append("\n");

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

		if (!ObjectUtil.isTrue(where.getRequired())) {
			content_mapper.append(getTab(tab));
			content_mapper.append("</if>").append("\n");
			content_count_mapper.append(getTab(tab));
			content_count_mapper.append("</if>").append("\n");
		}

	}

	public void appendAppends(int tab, List<AppendCustomSql> appends) {

		if (appends == null || appends.size() == 0) {
			return;
		}
		boolean isFirst = true;
		for (AppendCustomSql append : appends) {

			if (StringUtil.isNotTrimEmpty(append.getIfrule())) {
				content.append(getTab(tab));
				content.append("if(" + getFormatIfruleForContent(append.getIfrule()) + ") {").append("\n");
				content.append(getTab(tab + 1));

				content_mapper.append(getTab(tab));
				content_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(append.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");
				content_mapper.append(getTab(tab + 1));

				content_count_mapper.append(getTab(tab));
				content_count_mapper.append(
						"<if test=\"" + getFormatIfruleForMapper(append.getIfrule(), variablesForMapper) + "\" >")
						.append("\n");
				content_count_mapper.append(getTab(tab + 1));

			} else {
				content.append(getTab(tab));

				content_mapper.append(getTab(tab));
				content_count_mapper.append(getTab(tab));
			}

			StringBuffer sql = new StringBuffer();
			if (isFirst) {
				sql.append(" ");
			}
			sql.append(getAppendSql(append));
			sql.append(" ");

			String contentSql = formatCustomToContentSql(sql.toString(), tab);
			String mapperSql = formatCustomToMapperSql(sql.toString());

			content.append("appendSql.append(\"" + contentSql + "\");").append("\n");

			content_mapper.append(mapperSql).append("\n");
			content_count_mapper.append(mapperSql).append("\n");

			if (StringUtil.isNotTrimEmpty(append.getIfrule())) {
				content.append(getTab(tab));
				content.append("}").append("\n");

				content_mapper.append(getTab(tab));
				content_mapper.append("</if>").append("\n");
				content_count_mapper.append(getTab(tab));
				content_count_mapper.append("</if>").append("\n");
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

	public List<VariableValidate> getValidatesForMapper() {
		return validatesForMapper;
	}

	public List<Variable> getVariablesForMapper() {
		return variablesForMapper;
	}

	public List<Variable> getVariablesForParam() {
		return variablesForParam;
	}

	public Set<String> getTablesFromSQL(String sql) {
		Set<String> tables = new HashSet<String>();

		return tables;
	}

	public String formatCustomToContentSql(String sql, int tab) {
		if (sql != null && sql.indexOf("#{") >= 0) {

			sql = resolveToContentSQL(sql);

		}
		sql = resolveTableToContentSQL(sql, tab);
		return sql;
	}

	public String formatCustomToMapperSql(String sql) {
		if (sql != null && sql.indexOf("#{") >= 0) {

			sql = resolveToMapperSQL(sql, variablesForMapper);

		}
		return sql;
	}

	public String getFormatIfruleForContent(String ifrole) {
		return "ObjectUtil.isTrue(factory.getValueByJexlScript(\"" + ifrole + "\", data))";
	}

	public String getFormatIfruleForMapper(String ifrole, List<Variable> variables) {
		if (ifrole != null && (ifrole.indexOf(".") >= 0 || ifrole.indexOf("$") >= 0)) {
			String ifKey = base.getPlaceKey("if_key", keyCache);
			VariableBean variable = new VariableBean();
			variable.setValue(ifrole);
			variable.setName(ifKey);
			variables.add(variable);
			return ifKey + " == true or " + ifKey + " == 1";

		}
		if (ifrole != null) {
			ifrole = ifrole.replaceAll("&&", " and ");
		}
		return ifrole;
	}

	public String resolveTableToContentSQL(String sql, int tab) {
		if (StringUtil.isEmpty(sql)) {
			return sql;
		}
		StringBuffer result = new StringBuffer();
		result.append(sql);
		int lastIndex = 0;
		Pattern pattern = Pattern.compile("T\\{([^\\}]+)\\}");
		Matcher matcher = pattern.matcher(sql);
		boolean isFirst = true;
		while (matcher.find()) {
			String rule = matcher.group(1);
			int start = matcher.start();
			int end = matcher.end();
			if (isFirst) {
				result.setLength(0);
				isFirst = false;
			}
			result.append(sql.substring(lastIndex, start));
			lastIndex = end;
			String table = rule;
			if (shouldTableVariableDefinition(table)) {
				String code = getTableVariableDefinitionCode(table);

				content.append(getTab(tab));
				content.append(code).append("\n");

				result.append("\" + TABLE_" + table + " + \"");
			} else {
				result.append(table);
			}

		}

		if (lastIndex > 0 && lastIndex < sql.length()) {
			result.append(sql.substring(lastIndex));
		}
		return result.toString();
	}

	public String resolveToContentSQL(String sql) {
		if (StringUtil.isEmpty(sql)) {
			return sql;
		}
		StringBuffer result = new StringBuffer();
		result.append(sql);
		int lastIndex = 0;
		Pattern pattern = Pattern.compile("#\\{([^\\}]+)\\}");
		Matcher matcher = pattern.matcher(sql);
		boolean isFirst = true;
		while (matcher.find()) {
			String rule = matcher.group(1);
			int start = matcher.start();
			int end = matcher.end();
			if (isFirst) {
				result.setLength(0);
				isFirst = false;
			}
			result.append(sql.substring(lastIndex, start));
			lastIndex = end;
			boolean isSqlParam = false;
			if (start > 0) {
				if (sql.substring(start - 1, start).equals(":")) {
					if (result.length() > 0) {
						result.setLength(result.length() - 1);
					}
					isSqlParam = true;
				}
			}
			String placeKey = ruleCacheForContent.get(rule);
			if (placeKey == null) {
				placeKey = getPlaceKeyByRule(rule);
				ruleCacheForContent.put(rule, placeKey);
				if (isSqlParam) {
					VariableBean variable = new VariableBean();
					variable.setValue(rule);
					variable.setName(placeKey);
					variablesForParam.add(variable);
				}
			}
			if (isSqlParam) {
				placeKey = ":" + placeKey + "";
			} else {
				placeKey = "\" + factory.getValueByJexlScript(\"" + placeKey + "\", data) + \"";
			}

			result.append(placeKey);
		}

		if (lastIndex > 0 && lastIndex < sql.length()) {
			result.append(sql.substring(lastIndex));
		}
		return result.toString();
	}

	public String getPlaceKeyByRule(String rule) {
		String keyName = "place_key";
		if (rule != null) {
			rule = rule.replaceAll("[^0-9|A-Z|a-z|_|$]", "_");
		}
		if (StringUtil.isEmpty(rule)) {
			keyName = "place_key";
		} else {
			keyName = rule;
		}

		return base.getPlaceKey(keyName, keyCache);
	}

	public String resolveToMapperSQL(String sql, List<Variable> variables) {
		if (StringUtil.isEmpty(sql)) {
			return sql;
		}
		StringBuffer result = new StringBuffer();
		result.append(sql);
		int lastIndex = 0;
		Pattern pattern = Pattern.compile("#\\{([^\\}]+)\\}");
		Matcher matcher = pattern.matcher(sql);
		boolean isFirst = true;
		while (matcher.find()) {
			String rule = matcher.group(1);
			int start = matcher.start();
			int end = matcher.end();
			if (isFirst) {
				result.setLength(0);
				isFirst = false;
			}
			result.append(sql.substring(lastIndex, start));
			lastIndex = end;
			boolean isSqlParam = false;
			if (start > 0) {
				if (sql.substring(start - 1, start).equals(":")) {
					result.setLength(result.length() - 1);
					isSqlParam = true;
				}
			}
			String placeKey = ruleCacheForMapper.get(rule);
			if (placeKey == null) {
				placeKey = getPlaceKeyByRule(rule);
				ruleCacheForMapper.put(rule, placeKey);
				VariableBean variable = new VariableBean();
				variable.setValue(rule);
				variable.setName(placeKey);
				variables.add(variable);
			}
			if (isSqlParam) {
				placeKey = "#{" + placeKey + "}";
			} else {
				placeKey = "${" + placeKey + "}";
			}

			result.append(placeKey);
		}

		if (lastIndex > 0 && lastIndex < sql.length()) {
			result.append(sql.substring(lastIndex));
		}
		return result.toString();
	}
}
