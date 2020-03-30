package com.teamide.db.generater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.db.DBDataSource;
import com.teamide.db.bean.Database;
import com.teamide.util.IOUtil;
import com.teamide.util.StringUtil;

public class DatabaseGenerater {

	public DatabaseGenerater() {

	}

	public void generate(Database database) throws SQLException {

		JSONObject databaseJSON = (JSONObject) JSON.toJSON(database);
		Database rootDatabase = databaseJSON.toJavaObject(Database.class);
		String databaseName = "";
		if (database != null) {
			String url = database.getUrl();
			String host = url.substring(url.indexOf("://") + 3);
			if (host.indexOf("/") > 0) {
				databaseName = host.substring(host.indexOf("/") + 1);
				host = host.substring(0, host.indexOf("/"));
				if (databaseName.indexOf("?") > 0) {
					databaseName = databaseName.substring(0, databaseName.indexOf("?"));
				}
			}
			if (host.indexOf("?") > 0) {
				host = host.substring(0, host.indexOf("?"));
			}
			// 组合url
			String root_url = "jdbc:mysql://" + host + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT";

			// 设置数据库真实地址
			rootDatabase.setUrl(root_url);
		}

		if (!StringUtil.isEmpty(databaseName)) {

			DBDataSource dbDataSource = DBDataSource.create(rootDatabase);

			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = dbDataSource.getDataSource().getConnection();
				String sql = "CREATE DATABASE " + databaseName + ";";
				ps = conn.prepareStatement(sql);
				ps.execute();
			} catch (SQLException e) {
				if (e.getMessage() != null && e.getMessage().toLowerCase().indexOf("database exists") >= 0) {

				} else {
					throw e;
				}
			} finally {
				IOUtil.close(conn);
				dbDataSource.destroy();
			}
		}

	}

}
