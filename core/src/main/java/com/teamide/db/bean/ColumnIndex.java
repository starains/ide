package com.teamide.db.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库表的 索引信息
 */
public class ColumnIndex {

	private String name;

	private String column;

	private Boolean nonunique;

	private String type;
	// ----------------------------------------------------- Fields end

	/**
	 * 创建列对象
	 * 
	 * @param columnMetaResult
	 *            列元信息的ResultSet
	 * @return 列对象
	 */
	public static ColumnIndex create(ResultSet indexMetaResult) {

		return new ColumnIndex(indexMetaResult);
	}

	// ----------------------------------------------------- Constructor start
	public ColumnIndex() {

	}

	public ColumnIndex(ResultSet indexMetaResult) {

		try {
			init(indexMetaResult);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------- Constructor end

	/**
	 * 初始化
	 * 
	 * @param columnMetaResult
	 *            列的meta ResultSet
	 * @throws SQLException
	 *             SQL执行异常
	 */
	public void init(ResultSet indexMetaResult) throws SQLException {
		// 索引名称
		this.name = indexMetaResult.getString("INDEX_NAME");
		// 索引列名
		this.column = indexMetaResult.getString("COLUMN_NAME");
		// 索引值不唯一
		this.nonunique = indexMetaResult.getBoolean("NON_UNIQUE");
		// 索引类型
		this.type = indexMetaResult.getString("TYPE");

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNonunique() {

		if (nonunique != null && nonunique) {
			return true;
		}
		return true;
	}

	public void setNonunique(Boolean nonunique) {
		this.nonunique = nonunique;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

}
