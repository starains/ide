package com.teamide.service.impl;

import com.teamide.dao.IDao;
import com.teamide.db.DBDataSource;
import com.teamide.service.IService;

public class Service extends ServiceSave implements IService {

	public Service(DBDataSource dbDataSource, IDao dao) {
		super(dbDataSource, dao);
	}

	public Service(DBDataSource dbDataSource) {

		super(dbDataSource);

	}
}
