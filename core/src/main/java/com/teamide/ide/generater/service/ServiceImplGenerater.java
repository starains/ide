package com.teamide.ide.generater.service;

import com.teamide.app.AppContext;
import com.teamide.app.bean.ServiceBean;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

public class ServiceImplGenerater extends ServiceGenerater {
	protected final ServiceBean service;

	public ServiceImplGenerater(ServiceBean service, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(service, param, app, context);
		this.service = service;
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
	public String getMergePackage() {
		return super.getMergePackage() + ".impl";
	}

	@Override
	public String getMergeClassName() {
		return super.getMergeClassName().substring(1);
	}

	@Override
	public void buildData() {
		super.buildData();
		data.put("$i_package", super.getCodePackage());
		data.put("$i_classname", "I" + this.getClassName());

	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/service/impl/default";
	}

}
