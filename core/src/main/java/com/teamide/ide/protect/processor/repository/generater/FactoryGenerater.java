package com.teamide.ide.protect.processor.repository.generater;

import com.teamide.app.AppContext;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

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
		return "template/java/factory";
	}

}
