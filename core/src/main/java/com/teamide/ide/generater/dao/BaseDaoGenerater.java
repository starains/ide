package com.teamide.ide.generater.dao;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.ide.generater.BaseGenerater;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

public abstract class BaseDaoGenerater extends BaseGenerater {

	protected final DaoBean dao;

	public BaseDaoGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
		this.dao = dao;
	}

}
