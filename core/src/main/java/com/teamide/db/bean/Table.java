package com.teamide.db.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

/**
 * 数据库表信息
 *
 */
public class Table {

	@Column(name = "name")
	private String name;

	@Column(name = "comment")
	private String comment;

	@Column(name = "partitiontablerule")
	private String partitiontablerule;

	private final List<com.teamide.db.bean.Column> columns = new ArrayList<com.teamide.db.bean.Column>();

	private final List<com.teamide.db.bean.ColumnIndex> indexs = new ArrayList<com.teamide.db.bean.ColumnIndex>();

	public static Table create(String tableName) {

		return new Table(tableName);
	}

	// ----------------------------------------------------- Constructor start

	public Table() {

	}

	/**
	 * 构造
	 * 
	 * @param tableName
	 *            表名
	 */
	public Table(String name) {

		this.setName(name);
	}

	// ----------------------------------------------------- Constructor end

	// ----------------------------------------------------- Getters and Setters
	// start

	/**
	 * 获取表名
	 * 
	 * @return 表名
	 */
	public String getName() {

		return name;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}

	/**
	 * 设置表名
	 * 
	 * @param tableName
	 *            表名
	 */
	public void setName(String name) {

		this.name = name;
	}

	// ----------------------------------------------------- Getters and Setters
	// end

	/**
	 * 设置列对象
	 * 
	 * @param column
	 *            列对象
	 * @return 自己
	 */
	public Table addColumn(com.teamide.db.bean.Column column) {

		columns.add(column);
		return this;
	}

	public List<com.teamide.db.bean.Column> getColumns() {

		return columns;
	}

	public Table addIndex(com.teamide.db.bean.ColumnIndex index) {

		indexs.add(index);
		return this;
	}

	public String getPartitiontablerule() {

		return partitiontablerule;
	}

	public void setPartitiontablerule(String partitiontablerule) {

		this.partitiontablerule = partitiontablerule;
	}

	public List<com.teamide.db.bean.ColumnIndex> getIndexs() {
		return indexs;
	}

}
