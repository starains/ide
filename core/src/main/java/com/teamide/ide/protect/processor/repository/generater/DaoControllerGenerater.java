package com.teamide.ide.protect.processor.repository.generater;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

public class DaoControllerGenerater extends CodeGenerater {

	protected final DaoBean dao;

	public DaoControllerGenerater(DaoBean dao, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(dao, param, app, context);
		this.dao = dao;
	}

	public void generate() throws Exception {
		if (StringUtil.isEmpty(dao.getRequestmapping())) {
			return;
		}
		super.generate();
	}

	public String getPackage() {
		String pack = getControllerPackage();
		pack += ".dao";
		return pack;
	}

	public String getClassName() {
		return super.getClassName() + "Controller";
	}

	@Override
	public void buildData() {

		DaoGenerater daoGenerater = new DaoGenerater(dao, param, app, context);
		daoGenerater.init();
		data.put("$dao", daoGenerater.data);
	}

	@Override
	public String getTemplate() throws Exception {

		return "template/java/controller/dao";
	}

}
