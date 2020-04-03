package com.teamide.app.generater.factory;

import java.io.File;

import com.teamide.app.AppContext;
import com.teamide.app.generater.BaseGenerater;
import com.teamide.app.plugin.AppBean;

public class DatabaseFactoryGenerater extends BaseGenerater {

	public DatabaseFactoryGenerater(File sourceFolder, AppBean app, AppContext context) {
		super(null, sourceFolder, app, context);
	}

	public void appendContentCenter() throws Exception {
	}

	public String getPackage() {
		return getFactoryPackage();
	}

	@Override
	public void buildData() {

	}

	public String getClassName() {
		return getDatabaseFactoryClassname();
	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/factory/databaseFactory";
	}

}
