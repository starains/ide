package com.teamide.db;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teamide.db.partition.DatePartitionTable;
import com.teamide.db.partition.PkValuePartitionTable;
import com.teamide.util.StringUtil;
import com.teamide.db.bean.Table;

public class PartitionTableUtil {

	public static String getTablename(Table table, Map<String, Object> data) throws Exception {

		if (table == null) {
			throw new Exception("table is null");
		}
		String realtablename = table.getName();
		final String partitiontablerule = table.getPartitiontablerule();
		if (!StringUtil.isEmpty(partitiontablerule)) {
			realtablename = partitiontablerule;
			/**
			 * 示例
			 * 
			 * #{name}_#{pk%5}
			 * 
			 * #{name}_#{data(yyyy-MM-dd)}
			 * 
			 * #{name}_#{count(100)}
			 * 
			 * #{name}_#{columnName(%5)}
			 * 
			 */

			Pattern pattern = Pattern.compile("(#\\{)[^\\}]+(\\})");
			Matcher matcher = pattern.matcher(partitiontablerule);

			while (matcher.find()) {
				String matchParam = matcher.group();
				String matchRule = matchParam.replace("#{", "");
				matchRule = matchRule.replace("}", "");
				String resolverResult = resolverResult(matchRule, table, data);
				if (resolverResult == null) {
					resolverResult = "";
				}
				realtablename = realtablename.replace(matchParam, resolverResult);
			}

		}

		return realtablename;
	}

	private static String resolverResult(String matchRule, Table table, Map<String, Object> data) throws Exception {

		String result = "";
		if (!StringUtil.isEmpty(matchRule)) {
			matchRule = matchRule.trim();
			String partition = null;
			int over = 0;
			if (matchRule.indexOf("%") > 0) {
				String[] ms = matchRule.split("%");
				matchRule = ms[0];
				over = Integer.valueOf(ms[1]);
			}
			if (matchRule.equals("name")) {
				return table.getName();
			} else if (matchRule.equalsIgnoreCase("pk")) {
				partition = new PkValuePartitionTable().getPartition(matchRule, table, data);
				if (over <= 0) {
					over = 5;
				}
			} else if (matchRule.equalsIgnoreCase("data") || matchRule.indexOf("data(") == 0
					|| matchRule.indexOf("DATA(") == 0) {
				partition = new DatePartitionTable().getPartition(matchRule, table, data);
			} else if (matchRule.equalsIgnoreCase("count") || matchRule.indexOf("count(") == 0
					|| matchRule.indexOf("COUNT(") == 0) {
				int count = 100;
				int divisor = 100;
				if (count > divisor) {
					partition = "" + count / divisor;
				}
			} else {
				if (data != null && data.get(matchRule) != null) {
					partition = "" + data.get(matchRule);
				}
			}
			if (!StringUtil.isEmpty(partition)) {

				if (over > 0) {

					long n = Long.valueOf(partition);
					if (n < over) {
						result = "" + n;
					} else {
						result = "" + (n % over);
					}
				} else {
					result = partition;
				}
			}

		}

		return result;
	}

}
