package com.teamide.app.generater.jexl;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.generater.BaseGenerater;
import com.teamide.app.plugin.AppBean;

public class JexlProcessorGenerater extends BaseGenerater {

	public JexlProcessorGenerater(File sourceFolder, AppBean app, AppContext context) {
		super(null, sourceFolder, app, context);
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		return getJexlPackage();
	}

	@Override
	public void buildData() {
	}

	public String getClassName() {
		return getJexlProcessorClassname();
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/jexl/JexlProcessor";
	}

}
