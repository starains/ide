package com.teamide.app.generater.dao.merge;

import java.io.File;
import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.plugin.AppBean;

public class DaoImplMergeGenerater extends DaoMergeGenerater {

	public DaoImplMergeGenerater(String directory, List<DaoBean> daos, File sourceFolder, AppBean app,
			AppContext context) {
		super(directory, daos, sourceFolder, app, context);
	}

	@Override
	public String getCodePackage() {
		String pack = super.getCodePackage();
		pack += ".impl";
		return pack;
	}

	@Override
	public String getClassName() {
		return super.getClassName().substring(1);
	}

	@Override
	public void buildData() {
		super.buildData();
		data.put("$i_package", super.getCodePackage());
		data.put("$i_classname", "I" + this.getClassName());

	}

	public boolean isImpl() {
		return true;
	}

	@Override
	public String getTemplate() throws Exception {
		if (isUsemybatis()) {
			return "template/java/merge/dao/impl/mapper/default";
		}
		return "template/java/merge/dao/impl/default";
	}

}
