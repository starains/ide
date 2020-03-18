package com.teamide.ide.generater.dao.merge;

import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

public class DaoImplMergeGenerater extends DaoMergeGenerater {

	public DaoImplMergeGenerater(String directory, List<DaoBean> daos, RepositoryProcessorParam param, AppBean app,
			AppContext context) {
		super(directory, daos, param, app, context);
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

		return "template/java/merge/dao/impl/default";
	}

}
