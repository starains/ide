package com.teamide.db.ifaces;

import com.alibaba.fastjson.JSONObject;
import com.teamide.db.bean.Database;

public interface IDatabaseInitialize {

	public Database initialize(Database database, JSONObject data) throws Exception;
}
