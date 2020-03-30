package com.teamide.db.dialect;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.alibaba.fastjson.JSON;
import com.teamide.db.bean.Column;
import com.teamide.db.bean.Table;
import com.teamide.param.PageSqlParam;
import com.teamide.util.StringUtil;

/**
 * SQL方言，不同的数据库由于在某些SQL上有所区别，故为每种数据库配置不同的方言。<br>
 * 由于不同数据库间SQL语句的差异，导致无法统一拼接SQL，<br>
 * Dialect接口旨在根据不同的数据库，使用不同的方言实现类，来拼接对应的SQL，并将SQL和参数放入PreparedStatement中
 * 
 *
 */
public abstract class Dialect {

	private final TypeNames typeNames = new TypeNames();
	protected Wrapper wrapper = new Wrapper();

	public Dialect() {

		registerColumnType(Types.BOOLEAN, "char(1)");
		registerColumnType(Types.BIT, "bit");
		registerColumnType(Types.BIGINT, "bigint($l)");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TINYINT, "tinyint");
		registerColumnType(Types.INTEGER, "integer");
		registerColumnType(Types.CHAR, "char(1)");
		registerColumnType(Types.VARCHAR, "varchar($l)");
		registerColumnType(Types.FLOAT, "float");
		registerColumnType(Types.DOUBLE, "double precision");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.TIME, "time");
		registerColumnType(Types.TIMESTAMP, "timestamp");
		registerColumnType(Types.VARBINARY, "bit varying($l)");
		registerColumnType(Types.NUMERIC, "numeric($p,$s)");
		registerColumnType(Types.BLOB, "blob");
		registerColumnType(Types.CLOB, "clob");
		registerColumnType(Types.LONGVARCHAR, "text");
	}

	public Wrapper getWrapper() {

		return this.wrapper;
	}

	public void setWrapper(Wrapper wrapper) {

		this.wrapper = wrapper;
	}

	// -------------------------------------------- Execute

	public String getAddColumnString() {

		return "ADD COLUMN";
	}

	/**
	 * 映射数据库布尔值的SQL值。
	 */
	public String toBooleanValueString(boolean bool) {

		return bool ? "1" : "0";
	}

	public String getTypeName(int code) throws Exception {

		String result = typeNames.get(code);
		if (result == null) {
			throw new Exception("No default type mapping for (java.sql.Types) " + code);
		}
		return result;
	}

	public String getLongTextTypeName() {

		return "text";
	}

	public String getTypeName(int code, int length, int precision, int scale) throws Exception {

		if (code == Types.VARCHAR && length >= 5000) {
			return getLongTextTypeName();
		}
		String result = typeNames.get(code, length, precision, scale);
		if (result == null) {
			throw new Exception("No type mapping for java.sql.Types code: " + code + ", length: " + length);
		}
		return result;
	}

	protected void registerColumnType(int code, int capacity, String name) {

		typeNames.put(code, capacity, name);
	}

	protected void registerColumnType(int code, String name) {

		typeNames.put(code, name);
	}

	public String createTableString() {

		return "CREATE TABLE ";
	}

	public String getCreateTableColumntComment(String comment) {

		if (StringUtil.isEmpty(comment)) {
			return "";
		}

		return " COMMENT '" + comment + "' ";
	}

	public String getCreateTableComment(String comment) {

		if (StringUtil.isEmpty(comment)) {
			return "";
		}

		return " COMMENT = '" + comment + "' ";
	}

	public String sqlForSelectPrimaryKey(String name, String catalog, String schema) {

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT T.COLUMN_NAME as COLUMN_NAME ");
		sql.append(" FROM ");
		sql.append(" INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS T ");
		sql.append(" WHERE ");
		sql.append(" T.TABLE_NAME  = ");
		sql.append(" T.TABLE_NAME  = '" + name + "' ");
		if (StringUtil.isEmpty(catalog)) {
			sql.append(" AND T.TABLE_SCHEMA  = '" + schema + "' ");
		} else {
			sql.append(" AND T.TABLE_SCHEMA  = '" + catalog + "' ");
		}
		sql.append(" AND T.CONSTRAINT_NAME = 'PRIMARY' ");

		return sql.toString();
	}

	public String sqlForCreateDatabase(String name) throws Exception {

		StringBuffer sql = new StringBuffer();
		sql.append("CREATE DATABASE ");
		sql.append(wrapper.wrap(name));
		return sql.toString();
	}

	public String wrapName(String name) throws Exception {

		return wrapper.wrap(name);
	}

	public String sqlForCreateTable(String name, Table table) throws Exception {

		if (StringUtil.isEmpty(name)) {
			name = table.getName();
		}
		StringBuffer sql = new StringBuffer();
		sql.append(createTableString());
		sql.append(wrapper.wrap(name));
		List<Column> cs = table.getColumns();
		if (cs != null) {
			sql.append("(");
			List<Column> pks = new ArrayList<Column>();
			for (int i = 0; i < cs.size(); i++) {
				Column column = cs.get(i);
				if (column.isPrimarykey()) {
					pks.add(column);
				}
				sql.append(wrapper.wrap(column.getName()));
				sql.append(" ");
				try {
					String typeName = getTypeName(column.getType(), column.getLength(), column.getPrecision(),
							column.getScale());

					sql.append(typeName);
				} catch (Exception e) {
					throw new Exception("getTypeName error column:" + JSON.toJSONString(column), e);
				}
				sql.append(" ");

				if (!column.isNullable()) {
					sql.append(" NOT NULL ");
				}
				sql.append(getCreateTableColumntComment(column.getComment()));
				if (i < cs.size() - 1) {
					sql.append(",");
				}
			}

			if (pks.size() > 0) {
				sql.append(", PRIMARY KEY (");
				for (Column pk : pks) {
					sql.append(wrapper.wrap(pk.getName()));
					sql.append(",");
				}
				sql.setLength(sql.length() - 1);
				sql.append(")");
			}
			sql.append(")");
		}

		sql.append(getCreateTableComment(table.getComment()));
		return sql.toString();
	}

	public String getAlterTable() {

		return "ALTER TABLE";
	}

	public String getAddColumn() {

		return "ADD COLUMN";
	}

	public String getModifyColumn() {

		return "MODIFY COLUMN";
	}

	public String sqlAddTableColumn(String table, Column column) throws Exception {

		StringBuffer sql = new StringBuffer();
		sql.append(getAlterTable());
		sql.append(" ");
		sql.append(wrapper.wrap(table));
		sql.append(" ");

		sql.append(getAddColumn());
		sql.append(" ");
		sql.append(wrapper.wrap(column.getName()));
		sql.append(" ");

		String typeName = getTypeName(column.getType(), column.getLength(), column.getPrecision(), column.getScale());
		sql.append(typeName);
		sql.append(" ");

		if (!column.isNullable()) {
			sql.append(" NOT NULL ");
		}

		sql.append(getCreateTableColumntComment(column.getComment()));
		return sql.toString();
	}

	public String sqlUpdateTableColumn(String table, Column column) throws Exception {

		StringBuffer sql = new StringBuffer();
		sql.append(getAlterTable());
		sql.append(" ");
		sql.append(wrapper.wrap(table));
		sql.append(" ");

		sql.append(getModifyColumn());
		sql.append(" ");
		sql.append(wrapper.wrap(column.getName()));
		sql.append(" ");

		String typeName = getTypeName(column.getType(), column.getLength(), column.getPrecision(), column.getScale());
		sql.append(typeName);
		sql.append(" ");

		if (!column.isNullable()) {
			sql.append(" NOT NULL ");
		}

		sql.append(getCreateTableColumntComment(column.getComment()));
		return sql.toString();
	}

	public String sqlDropTablePrimaryKey(String table) {

		StringBuffer sql = new StringBuffer();
		sql.append(getAlterTable());
		sql.append(" ");

		sql.append(wrapper.wrap(table));

		sql.append(" DROP PRIMARY KEY ");
		return sql.toString();
	}

	public String sqlAddTablePrimaryKey(String table, List<Column> columns) {

		StringBuffer sql = new StringBuffer();
		sql.append(getAlterTable());
		sql.append(" ");

		sql.append(wrapper.wrap(table));

		sql.append(" ADD PRIMARY KEY ");
		if (columns != null) {
			List<Column> pks = new ArrayList<Column>();
			for (int i = 0; i < columns.size(); i++) {
				Column column = columns.get(i);
				if (column.isPrimarykey()) {
					pks.add(column);
				}
			}
			if (pks != null) {
				sql.append(" ( ");
				for (Column pk : pks) {
					sql.append(wrapper.wrap(pk.getName()));
					sql.append(",");
				}
				sql.setLength(sql.length() - 1);
				sql.append(") ");
			}
		}

		return sql.toString();
	}

	/**
	 * 根据不同数据库在查询SQL语句基础上包装其分页的语句<br>
	 * 各自数据库通过重写此方法实现最小改动情况下修改分页语句
	 * 
	 * @param sql
	 *            标准查询语句
	 * @param page
	 *            分页对象
	 * @return 分页语句
	 */
	public String wrapPageSql(String sql, PageSqlParam page) {
		StringBuffer pageSql = new StringBuffer(sql);
		// limit A offset B 表示：A就是你需要多少行，B就是查询的起点位置。
		pageSql.append(" LIMIT ").append(page.getPagesize()).append(" OFFSET ").append(getStartPosition(page));
		return pageSql.toString();
	}

	/**
	 * @return 开始位置
	 */
	public int getStartPosition(PageSqlParam page) {

		return getStartEnd(page)[0];
	}

	/**
	 * @return 结束位置
	 */
	public int getEndPosition(PageSqlParam page) {

		return getStartEnd(page)[1];
	}

	/**
	 * 开始位置和结束位置<br>
	 * 例如：<br>
	 * 页码：1，每页10 =》 [0, 10]<br>
	 * 页码：2，每页10 =》 [10, 20]<br>
	 * 。。。<br>
	 * 
	 * @return 第一个数为开始位置，第二个数为结束位置
	 */
	public int[] getStartEnd(PageSqlParam page) {

		return transToStartEnd(page);
	}

	/**
	 * 将页数和每页条目数转换为开始位置和结束位置<br>
	 * 此方法用于不包括结束位置的分页方法<br>
	 * 例如：<br>
	 * 页码：1，每页10 =》 [0, 10]<br>
	 * 页码：2，每页10 =》 [10, 20]<br>
	 * 。。。<br>
	 * 
	 * @return 第一个数为开始位置，第二个数为结束位置
	 */
	public int[] transToStartEnd(PageSqlParam page) {
		int pagesize = page.getPagesize();
		int pageindex = page.getPageindex();

		if (pageindex < 1) {
			pageindex = 1;
		}

		if (pagesize < 1) {
			pagesize = 0;
		}

		int start = (pageindex - 1) * pagesize;
		int end = start + pagesize;

		return new int[] { start, end };
	}

	public Table getTable(DataSource ds, String tableName) throws SQLException {
		List<Table> tables = getTables(ds, new String[] { tableName });
		if (tables.size() > 0) {
			return tables.get(0);
		}
		return null;
	}

	public abstract List<Table> getTables(DataSource ds, String... tableNames) throws SQLException;

	public abstract List<Table> getTables(DataSource ds) throws SQLException;
}
