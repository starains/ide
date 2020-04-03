package com.teamide.app.generater.dao;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.generater.BaseGenerater;
import com.teamide.app.plugin.AppBean;
import com.teamide.util.StringUtil;

public class DaoControllerGenerater extends BaseGenerater {

	protected final DaoBean dao;

	public DaoControllerGenerater(DaoBean dao, File sourceFolder, AppBean app, AppContext context) {
		super(dao, sourceFolder, app, context);
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
		data.put("$method_name", "invoke");

		DaoGenerater daoGenerater = new DaoGenerater(dao, sourceFolder, app, context);
		daoGenerater.init();
		data.put("$dao", daoGenerater.data);
	}

	@Override
	public String getTemplate() throws Exception {

		return "template/java/controller/dao";
	}

}
