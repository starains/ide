package com.teamide.db;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.teamide.annotation.Comment;
import com.teamide.db.bean.Column;
import com.teamide.db.bean.Table;
import com.teamide.db.dialect.Dialect;
import com.teamide.param.SqlParam;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.StringUtil;

public class BeanTableUtil {

	static final Map<Class<?>, Table> CLASS_TABLE_CACHE = new HashMap<Class<?>, Table>();

	static final Map<Class<?>, List<Column>> CLASS_COLUMNS_CACHE = new HashMap<Class<?>, List<Column>>();

	static final Map<Class<?>, Map<String, Field>> CLASS_KEYS_CACHE = new HashMap<Class<?>, Map<String, Field>>();

	public static Table getTable(Class<?> clazz) {

		return getTable(clazz, null);
	}

	public static Table getTableByAnnotationSuper(Class<?> clazz) {
		if (clazz == null || clazz.equals(Object.class)) {
			return null;
		}
		if (clazz.isAnnotationPresent(javax.persistence.Table.class)) {
			javax.persistence.Table annotation = clazz.getAnnotation(javax.persistence.Table.class);
			String name = annotation.name();
			if (!StringUtil.isEmpty(name)) {
				Table table = new Table(name);
				if (clazz.isAnnotationPresent(Comment.class)) {
					table.setComment(clazz.getAnnotation(Comment.class).value());
				}
				return table;
			}
		}
		return getTableByAnnotationSuper(clazz.getSuperclass());
	}

	public static Table getTable(Class<?> clazz, String tablename) {

		Table table = CLASS_TABLE_CACHE.get(clazz);
		if (table == null) {

			if (StringUtil.isNotEmpty(tablename)) {
				table = new Table(tablename);
			} else {
				table = getTableByAnnotationSuper(clazz);
			}
			if (table != null) {
				if (clazz.isAnnotationPresent(Comment.class)) {
					table.setComment(clazz.getAnnotation(Comment.class).value());
				}
				List<Column> columns = getColumns(clazz);
				for (Column column : columns) {
					table.addColumn(column);
				}
			}
		}

		CLASS_TABLE_CACHE.put(clazz, table);
		return table;
	}

	public static List<Column> getColumns(Class<?> clazz) {

		List<Column> columns = CLASS_COLUMNS_CACHE.get(clazz);
		if (columns == null) {
			columns = new ArrayList<Column>();
			Set<String> columnNames = new HashSet<String>();
			List<Field> fields = getColumnFields(clazz);
			if (fields != null) {
				for (Field field : fields) {
					Column column = getColumn(field);
					if (column != null) {
						if (!columnNames.contains(column.getName())) {
							columnNames.add(column.getName());
							columns.add(column);
						}
					}
				}
			}
		}
		CLASS_COLUMNS_CACHE.put(clazz, columns);
		return columns;
	}

	public static Column getColumn(Field field) {

		Column column = null;
		if (field.isAnnotationPresent(javax.persistence.Column.class)) {
			javax.persistence.Column columnAnnotation = field.getAnnotation(javax.persistence.Column.class);
			if (!StringUtil.isEmpty(columnAnnotation.name())) {
				column = new Column();
				column.setName(columnAnnotation.name());
				column.setNullable(columnAnnotation.nullable());
				column.setPrecision(columnAnnotation.precision());
				column.setScale(columnAnnotation.scale());
				column.setLength(columnAnnotation.length());
				column.setDefinition(columnAnnotation.columnDefinition());
				column.setType(field.getType());
				column.setField(field);
				if (field.getType().isEnum()) {
					column.setType(String.class);
				}
				if (field.isAnnotationPresent(javax.persistence.Id.class)) {
					column.setPrimarykey(true);
				}
				if (field.isAnnotationPresent(Comment.class)) {
					column.setComment(field.getAnnotation(Comment.class).value());
				}

				if (columnAnnotation.length() == 255 || columnAnnotation.length() < 1) {
					if (column.isPrimarykey()) {
						column.setLength(20);
					} else if (column.getType() == Types.BOOLEAN) {
						column.setLength(1);
					} else if (column.getType() == Types.INTEGER) {
						column.setLength(10);
					} else if (column.getType() == Types.BIGINT) {
						column.setLength(20);
					} else if (column.getType() == Types.VARCHAR) {
						column.setLength(250);
					}
				}
			}
		}

		return column;
	}

	public static Map<String, Field> getPrimaryKeys(Class<?> clazz) {

		Map<String, Field> keys = CLASS_KEYS_CACHE.get(clazz);
		if (keys == null) {
			keys = new HashMap<String, Field>();
			List<Column> columns = getColumns(clazz);
			for (Column column : columns) {
				if (column.isPrimarykey()) {
					keys.put(column.getName(), column.getField());
				}
			}
		}
		CLASS_KEYS_CACHE.put(clazz, keys);
		return keys;
	}

	public static Map<String, Object> getColumnData(Object object) {

		if (object == null) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
		List<Column> columns = getColumns(object.getClass());
		for (Column column : columns) {
			Object value = jsonObject.get(column.getField().getName());
			if (value != null) {
				data.put(column.getName(), value);
			}
		}
		return data;
	}

	public static <T> T toColumnObject(Class<T> clazz, Map<String, Object> data) {

		if (data == null) {
			return null;
		}
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(data);
		List<Column> columns = getColumns(clazz);
		for (Column column : columns) {
			for (String key : jsonObject.keySet()) {
				if (column.getName().equalsIgnoreCase(key)) {
					Object value = jsonObject.get(key);
					jsonObject.put(column.getField().getName(), value);
					break;
				}
			}
		}
		return JSONObject.toJavaObject(jsonObject, clazz);
	}

	public static List<Field> getColumnFields(Class<?> clazz) throws SecurityException {

		List<Field> fieldList = new ArrayList<Field>();
		fullColumnFields(clazz, fieldList);
		return fieldList;
	}

	public static void fullColumnFields(Class<?> clazz, List<Field> fieldList) throws SecurityException {

		if (clazz != null && clazz != Object.class) {
			Class<?> superClazz = clazz.getSuperclass();
			fullColumnFields(superClazz, fieldList);
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				boolean has = false;
				for (Field f : fieldList) {
					if (f.getName().equalsIgnoreCase(field.getName())) {
						has = true;
					}
				}
				if (!has) {
					fieldList.add(field);
				}
			}
		}
	}

	public static Map<String, Object> getKeyValueMap(Set<String> keys, Map<String, Object> columnvalues) {

		Map<String, Object> keyValue = new HashMap<String, Object>();
		for (String key : keys) {
			keyValue.put(key, columnvalues.get(key));
		}
		return keyValue;
	}

	public static SqlParam getUpdate(Object object, String tablename, Object old, DBDataSource dbDataSource)
			throws Exception {

		Map<String, Object> columnvalues = getColumnData(object);
		Map<String, Object> old_columnvalues = new HashMap<String, Object>();
		if (old != null) {
			old_columnvalues = getColumnData(old);
		}

		if (StringUtil.isEmpty(tablename)) {
			tablename = TableUtil.getRealtablename(object.getClass(), columnvalues, dbDataSource);

		}
		Set<String> keys = getPrimaryKeys(object.getClass()).keySet();
		Map<String, Object> keyValue = getKeyValueMap(keys, columnvalues);
		SqlParam param = new SqlParam(
				getUpdateSql(tablename, keyValue, columnvalues, old_columnvalues, dbDataSource.getDialect()),
				columnvalues);
		return param;
	}

	public static Object getKeyValue(Class<?> entityClass, Field key) {

		Object value = IDGenerateUtil.generate();
		return value;
	}

	public static void setKeyValue(Object object) {

		if (object == null) {
			return;
		}
		Map<String, Field> keys = getPrimaryKeys(object.getClass());
		for (String key : keys.keySet()) {
			Field field = keys.get(key);
			try {
				field.setAccessible(true);
				Object value = field.get(object);
				if (value == null || StringUtil.isEmpty(String.valueOf(value))) {
					field.set(object, getKeyValue(object.getClass(), field));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static SqlParam getInsert(Object object, String tablename, DBDataSource dbDataSource) throws Exception {

		setKeyValue(object);
		Map<String, Object> columnvalues = getColumnData(object);
		if (StringUtil.isEmpty(tablename)) {
			tablename = TableUtil.getRealtablename(object.getClass(), getColumnData(object), dbDataSource);

		}
		SqlParam param = new SqlParam(getInsertSql(tablename, columnvalues, dbDataSource.getDialect()), columnvalues);
		return param;
	}

	/**
	 * 获取新增语句 <一句话功能简述> <功能详细描述>
	 * 
	 * @param tablename
	 * @param key
	 * @param id
	 * @param params
	 * @return
	 * @throws Exception
	 * @see [类、类#方法、类#成员]
	 */
	public static String getInsertSql(String tablename, Map<String, Object> params, Dialect dialect) throws Exception {

		String sql = "INSERT INTO " + tablename + " ( ";
		String valueSql = "( ";
		int i = 0;
		for (String columnname : params.keySet()) {
			if (i == 0) {

			} else {
				sql += " ,";
				valueSql += " ,";
			}
			i++;
			if (dialect != null) {
				sql += dialect.getWrapper().wrap(columnname);
			} else {
				sql += columnname + " ";
			}
			if (params.get(columnname) == null || params.get(columnname).equals("")) {
				valueSql += "null";
			} else {
				valueSql += ":" + columnname;
			}
		}
		sql += ") VALUES " + valueSql + ")";
		return sql;
	}

	/**
	 * 获取更新语句 <一句话功能简述> <功能详细描述>
	 * 
	 * @param tablename
	 * @param key
	 * @param id
	 * @param params
	 * @return
	 * @throws Exception
	 * @see [类、类#方法、类#成员]
	 */
	public static String getUpdateSql(String tablename, Map<String, Object> keyNameValueMap, Map<String, Object> param,
			Map<String, Object> old_param, Dialect dialect) throws Exception {

		if (old_param == null) {
			old_param = new HashMap<String, Object>();
		}
		String sql = "UPDATE " + tablename + " set ";
		boolean setUpdateColumn = false;
		for (String columnname : param.keySet()) {
			if (keyNameValueMap.get(columnname) != null) {
				continue;
			}
			if (param.get(columnname) == null || param.get(columnname).equals("")) {
				if (dialect != null) {
					sql += " " + dialect.getWrapper().wrap(columnname);
				} else {
					sql += " " + columnname;
				}
				sql += "= null ,";
				setUpdateColumn = true;
			} else {
				boolean valueChange = false;
				if (old_param.get(columnname) != null) {
					if (!old_param.get(columnname).equals(param.get(columnname))) {
						valueChange = true;
					}
				} else {
					valueChange = true;
				}
				if (valueChange) {
					if (dialect != null) {
						sql += " " + dialect.getWrapper().wrap(columnname);
					} else {
						sql += " " + columnname;
					}
					sql += "=:" + columnname + " ,";
					setUpdateColumn = true;
				}
			}
		}
		if (!setUpdateColumn) {
			for (String columnname : param.keySet()) {
				if (keyNameValueMap.get(columnname) != null) {
					continue;
				}
				if (dialect != null) {
					sql += " " + dialect.getWrapper().wrap(columnname);
				} else {
					sql += " " + columnname;
				}

				sql += "=:" + columnname + " ,";
				break;
			}
		}
		if (sql.lastIndexOf(",") == sql.length() - 1) {
			sql = sql.substring(0, sql.length() - 1);
		}
		sql += " WHERE 1=1 ";
		for (String key : keyNameValueMap.keySet()) {
			sql += " AND " + key + "=:" + key;
			if (param.get(key) == null) {
				param.put(key, keyNameValueMap.get(key));
			}
		}
		return sql;
	}

}
