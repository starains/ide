package com.teamide.ide.generater.component;

import com.teamide.app.AppContext;
import com.teamide.ide.generater.BaseGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class ComponentDaoGenerater extends BaseGenerater {

	public ComponentDaoGenerater(RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(null, param, app, context);
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		return getComponentPackage();
	}

	@Override
	public void buildData() {

	}

	public String getClassName() {
		return getDaoComponentClassname();
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/component/dao";
	}

}
