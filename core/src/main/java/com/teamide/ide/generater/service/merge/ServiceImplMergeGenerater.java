package com.teamide.ide.generater.service.merge;

import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.ServiceBean;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

public class ServiceImplMergeGenerater extends ServiceMergeGenerater {

	public ServiceImplMergeGenerater(String directory, List<ServiceBean> services, RepositoryProcessorParam param,
			AppBean app, AppContext context) {
		super(directory, services, param, app, context);
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

	public boolean isImpl() {
		return true;
	}

	@Override
	public void buildData() {
		super.buildData();
		data.put("$i_package", super.getCodePackage());
		data.put("$i_classname", "I" + this.getClassName());

	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/merge/service/impl/default";
	}

}
