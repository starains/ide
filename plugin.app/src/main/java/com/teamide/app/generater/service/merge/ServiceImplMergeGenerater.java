package com.teamide.app.generater.service.merge;

import java.io.File;
import java.util.List;

import com.teamide.app.AppContext;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.plugin.AppBean;

public class ServiceImplMergeGenerater extends ServiceMergeGenerater {

	public ServiceImplMergeGenerater(String directory, List<ServiceBean> services, File sourceFolder, AppBean app,
			AppContext context) {
		super(directory, services, sourceFolder, app, context);
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
