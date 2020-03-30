package com.teamide.db.bean;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.Date;

/**
 * 数据库表的列信息
 */
public class Column {

	@javax.persistence.Column(name = "name")
	private String name;

	/** 类型，对应java.sql.Types中的类型 */
	@javax.persistence.Column(name = "type")
	private Integer type;

	@javax.persistence.Column(name = "length")
	private Integer length;

	@javax.persistence.Column(name = "nullable")
	private Boolean nullable;

	@javax.persistence.Column(name = "comment")
	private String comment;

	@javax.persistence.Column(name = "defaultvalue")
	private String defaultvalue;

	/** 数据长度 */
	@javax.persistence.Column(name = "precision")
	private Integer precision;

	/** 小数长度 */
	@javax.persistence.Column(name = "scale")
	private Integer scale;

	/** 定义 */
	@javax.persistence.Column(name = "definition")
	private String definition;

	@javax.persistence.Column(name = "primarykey")
	private Boolean primarykey;

	@javax.persistence.Column(name = "autoincrement")
	private Boolean autoincrement;

	private Field field;

	// ----------------------------------------------------- Fields end

	/**
	 * 创建列对象
	 * 
	 * @param columnMetaResult
	 *            列元信息的ResultSet
	 * @return 列对象
	 */
	public static Column create(ResultSet columnMetaResult) {

		return new Column(columnMetaResult);
	}

	// ----------------------------------------------------- Constructor start
	public Column() {

	}

	public Column(ResultSet columnMetaResult) {

		try {
			init(columnMetaResult);
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
	public void init(ResultSet columnMetaResult) throws SQLException {

		this.name = columnMetaResult.getString("COLUMN_NAME");
		this.type = columnMetaResult.getInt("DATA_TYPE");
		this.length = columnMetaResult.getInt("COLUMN_SIZE");
		int nullAble = columnMetaResult.getInt("NULLABLE");
		if (nullAble == 1) {
			this.nullable = true;
		}
		if (columnMetaResult.getObject("IS_NULLABLE") != null) {
			if ("YES".equalsIgnoreCase(columnMetaResult.getString("IS_NULLABLE"))) {
				this.nullable = true;
			}
		}
		if (columnMetaResult.getObject("IS_AUTOINCREMENT") != null) {
			if ("YES".equalsIgnoreCase(columnMetaResult.getString("IS_AUTOINCREMENT"))) {
				this.autoincrement = true;
			}
		}
		this.definition = columnMetaResult.getString("COLUMN_DEF");
		this.precision = columnMetaResult.getInt("ORDINAL_POSITION");
		this.comment = columnMetaResult.getString("REMARKS");

	}

	public void setType(Class<?> javaClass) {

		if (javaClass != null) {
			if (javaClass.equals(Boolean.class) || javaClass.equals(boolean.class)) {
				type = Types.BOOLEAN;
			} else if (javaClass.equals(Character.class) || javaClass.equals(char.class)) {
				type = Types.VARCHAR;
			} else if (javaClass.equals(Integer.class) || javaClass.equals(int.class)) {
				type = Types.INTEGER;
			} else if (javaClass.equals(Byte.class) || javaClass.equals(byte.class)) {
				type = Types.INTEGER;
			} else if (javaClass.equals(Long.class) || javaClass.equals(long.class)) {
				type = Types.BIGINT;
			} else if (javaClass.equals(Float.class) || javaClass.equals(float.class)) {
				type = Types.FLOAT;
			} else if (javaClass.equals(Double.class) || javaClass.equals(double.class)) {
				type = Types.DOUBLE;
			} else if (javaClass.equals(BigDecimal.class)) {
				type = Types.DECIMAL;
			} else if (javaClass.equals(BigInteger.class)) {
				type = Types.BIGINT;
			} else if (javaClass.equals(Date.class) || javaClass.equals(java.sql.Date.class)) {
				type = Types.DATE;
			} else if (javaClass.equals(Time.class)) {
				type = Types.TIME;
			} else if (javaClass.equals(byte[].class)) {
				type = Types.BLOB;
			} else {
				type = Types.VARCHAR;
			}
		} else {
			type = Types.VARCHAR;
		}
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public Integer getType() {

		return type;
	}

	public void setType(Integer type) {

		this.type = type;
	}

	public Integer getLength() {
		if (length == null) {
			return 0;
		}

		return length;
	}

	public void setLength(Integer length) {

		this.length = length;
	}

	public boolean isNullable() {

		if (nullable != null && nullable) {
			return true;
		}
		return false;
	}

	public void setNullable(Boolean nullable) {

		this.nullable = nullable;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}

	public String getDefaultvalue() {

		return defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {

		this.defaultvalue = defaultvalue;
	}

	public Integer getPrecision() {
		if (precision == null) {
			return 0;
		}

		return precision;
	}

	public void setPrecision(Integer precision) {

		this.precision = precision;
	}

	public Integer getScale() {
		if (scale == null) {
			return 0;
		}
		return scale;
	}

	public void setScale(Integer scale) {

		this.scale = scale;
	}

	public String getDefinition() {

		return definition;
	}

	public void setDefinition(String definition) {

		this.definition = definition;
	}

	public boolean isPrimarykey() {

		if (primarykey != null && primarykey) {
			return true;
		}
		return false;
	}

	public void setPrimarykey(Boolean primarykey) {

		this.primarykey = primarykey;
	}

	public Field getField() {

		return field;
	}

	public void setField(Field field) {

		this.field = field;
	}

	public boolean isAutoincrement() {
		if (autoincrement != null && autoincrement) {
			return true;
		}
		return false;
	}

	public void setAutoincrement(Boolean autoincrement) {
		this.autoincrement = autoincrement;
	}

}
