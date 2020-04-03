package com.teamide.app.generater.service;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.bean.ServiceBean;
import com.teamide.app.generater.BaseGenerater;
import com.teamide.app.plugin.AppBean;
import com.teamide.util.StringUtil;

public class ServiceControllerGenerater extends BaseGenerater {

	protected final ServiceBean service;

	public ServiceControllerGenerater(ServiceBean service, File sourceFolder, AppBean app, AppContext context) {
		super(service, sourceFolder, app, context);
		this.service = service;
	}

	public void generate() throws Exception {
		if (StringUtil.isEmpty(service.getRequestmapping())) {
			return;
		}
		super.generate();
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
		data.put("$method_name", "invoke");

		ServiceGenerater serviceGenerater = new ServiceGenerater(service, sourceFolder, app, context);
		serviceGenerater.init();
		data.put("$service", serviceGenerater.data);
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/controller/service";
	}

}
