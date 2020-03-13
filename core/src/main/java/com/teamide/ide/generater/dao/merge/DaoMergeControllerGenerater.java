package com.teamide.ide.generater.dao.merge;

import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.ide.generater.BaseMergeGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class DaoMergeControllerGenerater extends BaseMergeGenerater {

	protected final List<DaoBean> daos;

	public DaoMergeControllerGenerater(String directory, List<DaoBean> daos, RepositoryProcessorParam param,
			AppBean app, AppContext context) {
		super(directory, daos, param, app, context);
		this.daos = daos;
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

	}

	@Override
	public String getTemplate() throws Exception {

		return "template/java/merge/controller/dao";
	}

}
