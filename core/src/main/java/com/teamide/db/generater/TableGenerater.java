package com.teamide.db.generater;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.teamide.dao.IDao;
import com.teamide.dao.impl.Dao;
import com.teamide.db.DBDataSource;
import com.teamide.db.bean.Column;
import com.teamide.db.bean.Table;
import com.teamide.util.StringUtil;

public class TableGenerater {

	final DBDataSource dbDataSource;

	final IDao dao;

	public TableGenerater(DBDataSource dbDataSource) {

		this.dbDataSource = dbDataSource;
		this.dao = new Dao(dbDataSource);

	}

	private Table query(String name) throws Exception {

		Table table = dbDataSource.getDialect().getTable(dbDataSource.getDataSource(), name);
		return table;
	}

	public void generate(String name, Table table) throws Exception {

		if (StringUtil.isEmpty(name)) {
			name = table.getName();
		}

		Table old_table = query(name);
		// 表存在 则进行更新表
		if (old_table != null) {
			updateTable(name, table, old_table);
		} else {
			createTable(name, table);
		}
	}

	private void createTable(String name, Table table) throws Exception {

		String sql = dbDataSource.getDialect().sqlForCreateTable(name, table);
		dao.execute(sql);
	}

	private boolean updateTable(String tableName, Table new_table, Table old_table) throws Exception {

		if (StringUtil.isEmpty(tableName)) {
			tableName = new_table.getName();
		}
		// 查询字段
		Map<String, Column> new_colums = new HashMap<String, Column>();
		Set<Column> need_update = new HashSet<Column>();
		Set<Column> need_remove_key = new HashSet<Column>();
		Set<Column> need_add_key = new HashSet<Column>();
		boolean newFindKey = false;
		boolean oldFindKey = false;
		for (Column new_column : new_table.getColumns()) {
			String new_columnname = new_column.getName();
			if (StringUtil.isEmpty(new_columnname)) {
				continue;
			}
			if (new_column.isPrimarykey()) {
				newFindKey = true;
			}
			boolean has = false;
			for (Column old_column : old_table.getColumns()) {
				if (old_column.isPrimarykey()) {
					oldFindKey = true;
				}
				String old_columnname = old_column.getName();
				if (new_columnname.equalsIgnoreCase(old_columnname)) {
					has = true;
					// 新字段不是主键 老字段是主键
					if (!new_column.isPrimarykey() && old_column.isPrimarykey()) {
						need_remove_key.add(new_column);
					}
					// 新字段是主键 老字段不是主键
					if (new_column.isPrimarykey() && !old_column.isPrimarykey()) {
						need_add_key.add(new_column);
					}
					// if (new_colum.getLength() != old_column.getLength()) {
					// need_update.add(new_colum);
					// }
					if (!old_column.isPrimarykey() && !new_column.isPrimarykey()) {
						if (!old_column.isNullable() && new_column.isNullable()) {
							need_update.add(new_column);
						}
						if (old_column.isNullable() && !new_column.isNullable()) {
							need_update.add(new_column);
						}
					}
					break;
				}
			}
			if (!has) {
				new_colums.put(new_columnname, new_column);
			}
		}
		for (String new_columnname : new_colums.keySet()) {
			Column new_colum = new_colums.get(new_columnname);
			// 这里添加字段
			String sql = dbDataSource.getDialect().sqlAddTableColumn(tableName, new_colum);

			dao.execute(sql);
		}
		if (need_update.size() > 0) {
			for (Column update_column : need_update) {
				// 这里修改字段
				String sql = dbDataSource.getDialect().sqlUpdateTableColumn(tableName, update_column);

				dao.execute(sql);
			}
		}
		if (need_remove_key.size() > 0 || need_add_key.size() > 0) {

			if (oldFindKey) {
				// 这里移除主键
				String sql = dbDataSource.getDialect().sqlDropTablePrimaryKey(tableName);

				dao.execute(sql);
			}
			if (newFindKey) {
				// 这里添加主键
				String sql = dbDataSource.getDialect().sqlAddTablePrimaryKey(tableName, new_table.getColumns());

				dao.execute(sql);
			}

		}
		return false;
	}
}
