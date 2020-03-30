package com.teamide.db.dialect.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.teamide.db.bean.Column;
import com.teamide.db.bean.ColumnIndex;
import com.teamide.db.bean.Table;
import com.teamide.db.dialect.Dialect;
import com.teamide.util.IOUtil;

/**
 * ANSI SQL 方言
 *
 */
public class AnsiSqlDialect extends Dialect {

	@Override
	public List<Table> getTables(DataSource ds, String... tableNames) throws SQLException {
		Connection conn = null;
		ResultSet rs = null;
		List<Table> tables = new ArrayList<Table>();
		if (tableNames == null || tableNames.length == 0) {
			return tables;
		}

		try {
			conn = ds.getConnection();
			final DatabaseMetaData metaData = conn.getMetaData();

			if (tableNames.length == 1) {
				rs = metaData.getTables(conn.getCatalog(), null, tableNames[0], null);
			} else {
				rs = metaData.getTables(conn.getCatalog(), null, null, null);
			}

			while (rs.next()) {
				String name = rs.getString("TABLE_NAME");
				for (String tableName : tableNames) {

					if (name.equalsIgnoreCase(tableName)) {
						Table table = Table.create(tableName);
						tables.add(table);
					}
				}
			}
			if (tables.size() == 0) {
				return tables;
			}
			IOUtil.close(rs);

			for (Table table : tables) {

				table.setComment(getTableComment(conn, table.getName()));

				// 获得列
				List<Column> columns = getColumns(conn, table.getName());
				for (Column column : columns) {
					table.addColumn(column);
				}

				// 获得索引
				// 获得列
				List<ColumnIndex> columnIndexs = getColumnIndexs(conn, table.getName());
				for (ColumnIndex columnIndex : columnIndexs) {
					table.addIndex(columnIndex);
				}
				// 获得主键
				List<String> primaryKeys = getPrimaryKeys(conn, table.getName());
				for (String name : primaryKeys) {
					for (Column column : table.getColumns()) {
						if (column.getName().equalsIgnoreCase(name)) {
							column.setPrimarykey(true);
						}
					}
				}
			}
			return tables;

		} finally {
			IOUtil.close(rs, conn);
		}
	}

	public List<Column> getColumns(Connection conn, String tableName) throws SQLException {
		ResultSet rs = null;
		List<Column> columns = new ArrayList<Column>();
		try {
			final DatabaseMetaData metaData = conn.getMetaData();
			// 获得列
			rs = metaData.getColumns(conn.getCatalog(), null, tableName, null);
			while (rs.next()) {
				columns.add(Column.create(rs));
			}

		} finally {
			IOUtil.close(rs);
		}
		return columns;
	}

	public List<String> getPrimaryKeys(Connection conn, String tableName) throws SQLException {
		ResultSet rs = null;
		List<String> primaryKeys = new ArrayList<String>();
		try {
			final DatabaseMetaData metaData = conn.getMetaData();
			// 获得主键
			rs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName);
			while (rs.next()) {
				String name = rs.getString("COLUMN_NAME");
				primaryKeys.add(name);
			}

		} finally {
			IOUtil.close(rs);
		}
		return primaryKeys;
	}

	public List<ColumnIndex> getColumnIndexs(Connection conn, String tableName) throws SQLException {
		ResultSet rs = null;
		List<ColumnIndex> columnIndexs = new ArrayList<ColumnIndex>();
		try {
			final DatabaseMetaData metaData = conn.getMetaData();
			// 获得索引
			rs = metaData.getIndexInfo(conn.getCatalog(), null, tableName, false, false);
			while (rs.next()) {
				columnIndexs.add(ColumnIndex.create(rs));
			}

		} finally {
			IOUtil.close(rs);
		}
		return columnIndexs;
	}

	public String getTableComment(Connection conn, String tableName) throws SQLException {
		return null;
	}

	@Override
	public List<Table> getTables(DataSource ds) throws SQLException {
		Connection conn = null;
		ResultSet rs = null;
		List<Table> tables = new ArrayList<Table>();

		try {
			conn = ds.getConnection();
			final DatabaseMetaData metaData = conn.getMetaData();

			rs = metaData.getTables(conn.getCatalog(), null, null, null);

			while (rs.next()) {
				String name = rs.getString("TABLE_NAME");
				Table table = Table.create(name);
				tables.add(table);
			}
			if (tables.size() == 0) {
				return tables;
			}
			IOUtil.close(rs);

			for (Table table : tables) {

				table.setComment(getTableComment(conn, table.getName()));

				// 获得列
				List<Column> columns = getColumns(conn, table.getName());
				for (Column column : columns) {
					table.addColumn(column);
				}

				// 获得索引
				// 获得列
				List<ColumnIndex> columnIndexs = getColumnIndexs(conn, table.getName());
				for (ColumnIndex columnIndex : columnIndexs) {
					table.addIndex(columnIndex);
				}
				// 获得主键
				List<String> primaryKeys = getPrimaryKeys(conn, table.getName());
				for (String name : primaryKeys) {
					for (Column column : table.getColumns()) {
						if (column.getName().equalsIgnoreCase(name)) {
							column.setPrimarykey(true);
						}
					}
				}
			}
			return tables;

		} finally {
			IOUtil.close(rs, conn);
		}
	}
	// ----------------------------------------------------------------------------
	// Protected method start
	// ----------------------------------------------------------------------------
	// Protected method end

}
