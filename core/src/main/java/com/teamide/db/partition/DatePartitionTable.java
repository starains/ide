package com.teamide.db.partition;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.teamide.db.bean.Table;

public class DatePartitionTable implements PartitionTable {

	@Override
	public String getPartition(String rule, Table table, Map<String, Object> data) throws Exception {

		String format = "yyyyMMddHH";
		int first = rule.indexOf("(");
		int end = rule.indexOf(")");

		if (first > 0 && end > first) {
			format = rule.substring(first + 1, end);
		}
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format.trim());
		return dateFormat.format(date);
	}

}
