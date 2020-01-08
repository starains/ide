package com.teamide.protect.ide.processor.repository.generater;

import com.teamide.app.AppContext;
import com.teamide.util.StringUtil;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.project.AppBean;

public class FactoryGenerater extends CodeGenerater {

	public FactoryGenerater(RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(null, param, app, context);
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		String pack = app.getOption().getFactorypackage();
		if (StringUtil.isEmpty(pack)) {
			pack = getBasePackage() + ".factory";
		}
		return pack;
	}

	@Override
	public void buildData() {
		// TODO Auto-generated method stub

	}

	public String getClassName() {
		return "AppFactory";
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/factory";
	}

}
