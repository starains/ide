package com.teamide.app.generater.dao;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.generater.BaseGenerater;
import com.teamide.app.plugin.AppBean;

public abstract class BaseDaoGenerater extends BaseGenerater {

	protected final DaoBean dao;

	public BaseDaoGenerater(DaoBean dao, File sourceFolder, AppBean app, AppContext context) {
		super(dao, sourceFolder, app, context);
		this.dao = dao;
	}

}
