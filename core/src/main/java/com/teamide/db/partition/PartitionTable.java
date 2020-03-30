package com.teamide.db.partition;

import java.util.Map;

import com.teamide.db.bean.Table;

public interface PartitionTable {

	public String getPartition(String rule, Table table, Map<String, Object> data) throws Exception;

}
