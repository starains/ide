package com.teamide.ide.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.DatabaseBean;
import com.teamide.ide.configure.IDEOptions;
import com.teamide.ide.enums.DatabaseType;
import com.teamide.ide.factory.IDEFactory;
import com.teamide.ide.util.TokenUtil;
import com.teamide.param.SqlParam;
import com.teamide.param.SqlParamList;
import com.teamide.util.StringUtil;

@Resource
public class DatabaseService extends BaseService<DatabaseBean> {

	public List<DatabaseBean> query(String userid, DatabaseType type) throws Exception {
		return query(userid, type.name());
	}

	public List<DatabaseBean> query(String userid, String type) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userid", userid);
		if (type != null) {
			param.put("type", type);
		}
		List<DatabaseBean> databases = queryList(param);
		return databases;
	}

	@Override
	public DatabaseBean insert(ClientSession session, DatabaseBean t) throws Exception {
		initDatabase(t);
		return super.insert(session, t);
	}

	public void initDatabase(DatabaseBean t) throws Exception {
		if (StringUtil.isEmpty(t.getType())) {
			throw new Exception("类型不能为空！");
		}
		DatabaseType databaseType = DatabaseType.valueOf(t.getType());
		switch (databaseType) {
		case JDBC:

			String username = TokenUtil.getRandom(16);
			String password = TokenUtil.getRandom(16);
			String database = TokenUtil.getRandom(16).toUpperCase();

			SqlParamList sqlParams = new SqlParamList();

			String sql = "CREATE DATABASE IF NOT EXISTS " + database
					+ " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";
			Map<String, Object> param = new HashMap<String, Object>();
			SqlParam sqlParam = new SqlParam(sql, param);
			sqlParams.add(sqlParam);

			sql = "CREATE USER '" + username + "'@'127.0.0.1' IDENTIFIED BY '" + password + "'";
			param = new HashMap<String, Object>();
			sqlParam = new SqlParam(sql, param);
			sqlParams.add(sqlParam);

			sql = "GRANT ALL privileges ON " + database + ".* TO '" + username + "'@'127.0.0.1'";
			param = new HashMap<String, Object>();
			sqlParam = new SqlParam(sql, param);
			sqlParams.add(sqlParam);

			IDEFactory.getService().execute(sqlParams);
			String url = IDEOptions.get().getJdbc().getUrl();

			String start = "jdbc:mysql://";
			if (url.indexOf("/", start.length()) > 0) {
				url = url.substring(0, url.indexOf("/", start.length()));
			}
			if (url.indexOf("?") > 0) {
				url = url.substring(0, url.indexOf("?"));
			}
			url += "/" + database + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT&useSSL=false";

			JSONObject option = new JSONObject();
			option.put("username", username);
			option.put("password", password);
			option.put("url", url);

			t.setOption(option.toJSONString());

			break;

		}
	}

	@Override
	public DatabaseBean update(ClientSession session, DatabaseBean t) throws Exception {
		return super.update(session, t);
	}

}
