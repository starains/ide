package com.teamide.ide.generater.service.merge;

import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.ServiceBean;
import com.teamide.ide.generater.BaseMergeGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class ServiceMergeControllerGenerater extends BaseMergeGenerater {

	protected final List<ServiceBean> services;

	public ServiceMergeControllerGenerater(String directory, List<ServiceBean> services, RepositoryProcessorParam param,
			AppBean app, AppContext context) {
		super(directory, services, param, app, context);
		this.services = services;
	}

	public String getPackage() {
		String pack = getControllerPackage();
		pack += ".service";
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
		return "template/java/merge/controller/service";
	}

}
