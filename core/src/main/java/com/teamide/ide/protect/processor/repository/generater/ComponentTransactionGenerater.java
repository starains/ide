package com.teamide.ide.protect.processor.repository.generater;

import com.teamide.app.AppContext;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class ComponentTransactionGenerater extends CodeGenerater {

	public ComponentTransactionGenerater(RepositoryProcessorParam param, AppBean app, AppContext context) {
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
		return getTransactionComponentClassname();
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/component/transaction";
	}

}
