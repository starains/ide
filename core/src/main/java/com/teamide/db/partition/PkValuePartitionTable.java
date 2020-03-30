package com.teamide.db.partition;

import java.util.Map;

import com.teamide.db.bean.Column;
import com.teamide.db.bean.Table;

public class PkValuePartitionTable implements PartitionTable {

	@Override
	public String getPartition(String rule, Table table, Map<String, Object> data) throws Exception {

		if (data != null && table != null && table.getColumns() != null) {
			for (Column column : table.getColumns()) {
				if (column.isPrimarykey()) {
					if (data.get(column.getName()) != null) {
						return "" + data.get(column.getName());
					}
				}
			}
		}
		return null;
	}

}
